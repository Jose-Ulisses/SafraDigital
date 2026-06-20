import pandas as pd
import joblib
import matplotlib.pyplot as plt
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, mean_absolute_percentage_error, r2_score

df_raw = pd.read_excel("drive/MyDrive/SafraDigitalPredicao/tabelaDadosColheita2002.xlsx", header=None)

anos_linha = df_raw.iloc[4].values
valores_linha = df_raw.iloc[6].values

dados_producao = []
for i in range(len(anos_linha)):
    if i % 2 == 1:
        ano = int(anos_linha[i-1])
        prod_toneladas = float(valores_linha[i])

        prod_sacas = prod_toneladas * 16.6667
        dados_producao.append({"Ano": ano, "Producao_Sacas": prod_sacas})

df_prod = pd.DataFrame(dados_producao).sort_values("Ano").reset_index(drop=True)

tabelas = pd.read_html("drive/MyDrive/SafraDigitalPredicao/dadosClimaticos2002.xls")
df_clima = tabelas[0]

df_clima.columns = [col[1] for col in df_clima.columns]

colunas_para_ajustar = ['Temperatura (°C)', 'Temp Min (°C)', 'Temp Max (°C)', 'Chuva (mm)', 'Umidade (%)', 'Umid Min (%)', 'Umid Max (%)']
for col in colunas_para_ajustar:
    df_clima[col] = pd.to_numeric(df_clima[col], errors='coerce')
    df_clima[col] = df_clima[col] / 10.0

df_clima[colunas_para_ajustar] = df_clima[colunas_para_ajustar].fillna(df_clima[colunas_para_ajustar].mean())

df_clima['Data'] = df_clima['Data'].astype(str).str.strip()
df_clima['Data'] = pd.to_datetime(df_clima['Data'], format='%d/%m/%Y')
df_clima['Ano'] = df_clima['Data'].dt.year
df_clima['Mes'] = df_clima['Data'].dt.month

clima_geral_anual = df_clima.groupby('Ano').agg(
    Chuva_Anual_Total=('Chuva (mm)', 'sum'),
    Temp_Max_Media_Anual=('Temp Max (°C)', 'mean'),
    Umid_Min_Media_Anual=('Umid Min (%)', 'mean')
).reset_index()

chuva_florada = df_clima[df_clima['Mes'].isin([9, 10])].groupby('Ano')['Chuva (mm)'].sum().reset_index()
chuva_florada.columns = ['Ano', 'Chuva_Florada_Set_Out']

calor_verao = df_clima[df_clima['Mes'].isin([12, 1, 2])].groupby('Ano')['Temp Max (°C)'].mean().reset_index()
calor_verao.columns = ['Ano', 'Temp_Max_Verao']

clima_consolidado = pd.merge(clima_geral_anual, chuva_florada, on='Ano')
clima_consolidado = pd.merge(clima_consolidado, calor_verao, on='Ano')

df_prod['Lag_1'] = df_prod['Producao_Sacas'].shift(1)
df_prod['Lag_2'] = df_prod['Producao_Sacas'].shift(2)

prod_2024_real = df_prod[df_prod['Ano'] == 2024]['Producao_Sacas'].values[0]
prod_2023_real = df_prod[df_prod['Ano'] == 2023]['Producao_Sacas'].values[0]

df_dataset = pd.merge(df_prod, clima_consolidado, on='Ano', how='right')

df_dataset.loc[df_dataset['Ano'] == 2025, 'Lag_1'] = prod_2024_real
df_dataset.loc[df_dataset['Ano'] == 2025, 'Lag_2'] = prod_2023_real

df_treino = df_dataset.dropna(subset=['Producao_Sacas', 'Lag_1', 'Lag_2']).copy()

atributos = ['Lag_1', 'Lag_2', 'Chuva_Anual_Total', 'Chuva_Florada_Set_Out', 'Temp_Max_Verao', 'Umid_Min_Media_Anual']
X_train = df_treino[atributos]
y_train = df_treino['Producao_Sacas']

model = RandomForestRegressor(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

X_future_2025 = df_dataset[df_dataset['Ano'] == 2025][atributos]
predicao_2025 = model.predict(X_future_2025)[0]

print("\n" + "="*55)
print(f"   PREDIÇÃO INTELIGENTE PARA A SAFRA DE 2025 (REGIONAL)")
print("="*55)
print(f" Modelo Alimentado com: Histórico + Bienalidade + Clima Diário")
print(f" -> Estimativa de Colheita Calculada: {predicao_2025:,.0f} sacas")
print("="*55)

y_pred_treino = model.predict(X_train)
mae = mean_absolute_error(y_train, y_pred_treino)
mape = mean_absolute_percentage_error(y_train, y_pred_treino) * 100
r2 = r2_score(y_train, y_pred_treino)

print("\n" + "="*55)
print(f"   PREDIÇÃO INTELIGENTE PARA A SAFRA DE 2025: {predicao_2025:,.0f} sacas")
print("="*55)
print(f" Erro Médio Absoluto (MAE): {mae:,.0f} sacas")
print(f" Erro Percentual Médio (MAPE): {mape:.2f}%")
print(f" Coeficiente de Determinação (R²): {r2:.4f}")
print("="*55)

df_comparativo = pd.DataFrame({
    'Ano': df_treino['Ano'].astype(int),
    'Real (Sacas)': y_train.round(0),
    'Previsto (Sacas)': y_pred_treino.round(0)
})
df_comparativo['Erro Abs (%)'] = (abs(df_comparativo['Real (Sacas)'] - df_comparativo['Previsto (Sacas)']) / df_comparativo['Real (Sacas)']) * 100

print("\n--- TABELA DE VALIDAÇÃO HISTÓRICA ---")
print(df_comparativo.to_string(index=False, formatters={'Real (Sacas)': '{:,.0f}'.format, 'Previsto (Sacas)': '{:,.0f}'.format, 'Erro Abs (%)': '{:.2f}%'.format}))

plt.figure(figsize=(10, 5))
plt.plot(df_comparativo['Ano'], df_comparativo['Real (Sacas)'], marker='o', label='Histórico Real (IBGE)', color='#1f77b4', linewidth=2)
plt.plot(df_comparativo['Ano'], df_comparativo['Previsto (Sacas)'], marker='x', linestyle='--', label='Ajuste do Modelo (Predito)', color='#ff7f0e', linewidth=2)
plt.scatter([2025], [predicao_2025], color='red', s=150, zorder=5, label=f'Predição 2025 ({predicao_2025:,.0f} sacas)')

plt.title('Validação do Modelo de IA: Safra Real vs Estimada em Nova Resende (MG)', fontsize=14, pad=15)
plt.xlabel('Ano da Safra', fontsize=12)
plt.ylabel('Quantidade de Sacas (60kg)', fontsize=12)
plt.xticks(df_dataset['Ano'].unique())
plt.grid(True, linestyle=':', alpha=0.6)
plt.legend(fontsize=11)
plt.tight_layout()
plt.show()

dados_2025 = df_dataset[df_dataset['Ano'] == 2025].iloc[0]

print("=== COPIE ESTES VALORES PARA O SEU APP ANDROID ===")
print(f"Chuva Anual: {dados_2025['Chuva_Anual_Total']:.2f}")
print(f"Chuva Florada: {dados_2025['Chuva_Florada_Set_Out']:.2f}")
print(f"Calor Verão: {dados_2025['Temp_Max_Verao']:.2f}")
print(f"Umidade Mínima: {dados_2025['Umid_Min_Media_Anual']:.2f}")
print(f"IBGE Lag 1 (2024): {dados_2025['Lag_1']:.2f}")
print(f"IBGE Lag 2 (2023): {dados_2025['Lag_2']:.2f}")

model.fit(X_train, y_train)

joblib.dump(model, 'modelo_safra.joblib')