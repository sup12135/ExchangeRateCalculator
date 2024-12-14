import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, accuracy_score

# 1. 데이터 준비
def generate_data_with_economic_factors(file_path):
    # 데이터 로드
    data = pd.read_csv(file_path)

    # Date 열 제거 (필요 없는 열)
    data = data.drop(columns=["Date"], errors="ignore")

    # 타겟 변수 생성 (조언 메시지 로직 적용)
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

    # 입력 변수(X)와 타겟 변수(y) 분리
    X = data[["USD_KRW", "KOSPI", "KOSDAQ", "Crude_Oil_Price", "Fed_Funds_Rate", "Inflation_Rate"]]
    y = data["advice"]

    return X, y

# 2. 모델 학습
def train_model(X, y):
    # 데이터 분리
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # 랜덤 포레스트 모델 학습
    model = RandomForestClassifier(random_state=42, n_estimators=100)
    model.fit(X_train, y_train)

    # 예측
    y_pred = model.predict(X_test)

    # 평가
    print("모델 정확도:", accuracy_score(y_test, y_pred))
    print("\n분류 보고서:\n", classification_report(y_test, y_pred))

    return model

# 3. 조언 생성
def generate_advice(model, new_data):
    # 새로운 데이터로 예측
    predictions = model.predict(new_data)
    new_data["advice"] = predictions
    return new_data

# 4. 실행
if __name__ == "__main__":
    # 파일 경로 설정 (로컬 데이터 파일)
    file_path = "/Users/songdongjun/Downloads/data.csv"

    try:
        # 데이터 준비
        X, y = generate_data_with_economic_factors(file_path)

        # 모델 학습
        model = train_model(X, y)

        # 새로운 데이터로 조언 생성
        new_data = pd.DataFrame({
            "USD_KRW": [1350, 1450, 1290],
            "KOSPI": [2500, 2400, 2600],
            "KOSDAQ": [700, 680, 720],
            "Crude_Oil_Price": [75, 85, 70],
            "Fed_Funds_Rate": [2.5, 3.5, 2.0],
            "Inflation_Rate": [2.0, 3.0, 1.5]
        })
        advice_results = generate_advice(model, new_data)
        print("\n새로운 데이터에 대한 조언:\n", advice_results)

    except FileNotFoundError:
        print(f"파일을 찾을 수 없습니다. 경로를 확인하세요: {file_path}")
    except Exception as e:
        print(f"오류가 발생했습니다: {e}")
