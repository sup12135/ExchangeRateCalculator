import pandas as pd
import numpy as np
import datetime
import requests
import joblib
from sklearn.model_selection import train_test_split

# 데이터 생성 기간 설정
start_date = datetime.datetime(2022, 1, 1)
end_date = datetime.datetime(2024, 11, 30)
date_range = pd.date_range(start=start_date, end=end_date, freq='D')

# 난수 생성기의 시드 고정 for reproducibility
np.random.seed(42)

# 데이터프레임 생성
data = {
    'Date': date_range,
    'USD_KRW': np.random.uniform(1100, 1400, len(date_range)),
    'JPY_KRW': np.random.uniform(9.5, 11.5, len(date_range)),
    'CNY_KRW': np.random.uniform(160, 180, len(date_range)),
    'EUR_KRW': np.random.uniform(1300, 1600, len(date_range)),
    'NASDAQ_Index': np.random.uniform(10000, 15000, len(date_range)),
    'S&P500_Index': np.random.uniform(3500, 4500, len(date_range)),
    'Crude_Oil_Price': np.random.uniform(50, 100, len(date_range)),
    'GDP_Growth_Rate': np.random.uniform(1, 4, len(date_range)),
    'CPI': np.random.uniform(100, 130, len(date_range))
}

# 데이터프레임으로 변환
df = pd.DataFrame(data)

# 데이터 전처리
features = df.drop(columns=['Date', 'USD_KRW'])

# 특정 금액에 대한 환전 추천 로직
def recommend_exchange_rate(current_rate, predicted_rate):
    if predicted_rate > current_rate * 1.02:
        return "환전을 추천하지 않습니다. 환율이 상승할 것으로 예상됩니다."
    elif predicted_rate < current_rate * 0.98:
        return "환전을 추천합니다. 환율이 하락할 것으로 예상됩니다."
    else:
        return "환전 여부는 현재 상황에 따라 신중히 고려하시기 바랍니다."

# 모델 불러오기
model_path = '/Users/songdongjun/Desktop/ExchangeRateCalculator/ExchangeCal/scripts/random_forest_model.joblib'
model = joblib.load(model_path)

# REST API를 통해 실시간 환율 데이터 가져오기
try:
    response = requests.get("http://localhost:8080/api/exchange-rate/USD")  # 자바 서버의 엔드포인트 URL
    response.raise_for_status()
    real_time_data = response.json()
    real_time_usd_krw = real_time_data['rates']['KRW']
except Exception as e:
    print(f"실시간 환율 데이터를 가져오는 데 실패했습니다: {e}")
    real_time_usd_krw = None

# 실시간 데이터를 데이터프레임에 추가
if real_time_usd_krw is not None:
    last_features = features.iloc[-1].values.reshape(1, -1)
    last_rate = real_time_usd_krw
    next_rate_prediction = model.predict(last_features)[0]

    # 추천 출력
    recommendation = recommend_exchange_rate(last_rate, next_rate_prediction)
    print(f"현재 실시간 환율: {last_rate}")
    print(f"예측된 다음 환율: {next_rate_prediction}")
    print(f"추천: {recommendation}")
else:
    print("실시간 환율 데이터를 사용할 수 없습니다.")

