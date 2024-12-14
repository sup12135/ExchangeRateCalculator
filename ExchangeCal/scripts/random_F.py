import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import joblib

# 1. 데이터 로드
data = pd.read_csv("/Users/songdongjun/Downloads/data.csv")

# 2. 'advice' 열 생성 (조언 메시지 로직 적용)
def create_advice(exchange_rate, kospi, kosdaq, oil_price, fed_rate, inflation_rate):
    threshold_exchange_rate = 1300.0
    threshold_oil_price = 80.0
    threshold_fed_rate = 3.0
    threshold_inflation_rate = 2.5

    if exchange_rate > threshold_exchange_rate:
        if oil_price > threshold_oil_price:
            return "환율 상승과 원유 가격 상승으로 수입 물가 상승 우려, 신중히 환전하세요."
        else:
            return "환율 상승 중이지만 원유 가격 안정으로 긍정적인 요소가 있습니다."
    else:
        if fed_rate < threshold_fed_rate and inflation_rate < threshold_inflation_rate:
            return "환율 안정 상태로 투자 기회로 적합합니다."
        else:
            return "환율 안정 상태지만 고금리로 인한 투자 리스크 검토가 필요합니다."

# 타겟 변수 생성
data["advice"] = data.apply(
    lambda row: create_advice(
        row["USD_KRW"],
        row["KOSPI"],
        row["KOSDAQ"],
        row["Crude_Oil_Price"],
        row["Fed_Funds_Rate"],
        row["Inflation_Rate"]
    ),
    axis=1
)

# 3. 독립 변수(X)와 종속 변수(y) 분리
X = data[["USD_KRW", "KOSPI", "KOSDAQ", "Crude_Oil_Price", "Fed_Funds_Rate", "Inflation_Rate"]]
y = data["advice"]  # 타겟 변수

# 4. 학습/테스트 데이터 분리
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 5. 랜덤 포레스트 모델 학습
model = RandomForestClassifier(random_state=42)
model.fit(X_train, y_train)

# 6. 모델 평가
y_pred = model.predict(X_test)
print("모델 정확도:", accuracy_score(y_test, y_pred))
print("\n분류 보고서:\n", classification_report(y_test, y_pred))

# 7. 모델 저장
joblib.dump(model, "advice_model.sav")
print("모델이 'advice_model.sav' 파일로 저장되었습니다.")
