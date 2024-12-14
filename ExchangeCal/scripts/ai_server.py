from flask import Flask, request, jsonify
import joblib
import pandas as pd

# 1. Flask 앱 초기화
app = Flask(__name__)

# 2. 모델 로드
model = joblib.load("ExchangeCal/src/main/java/com/example/exchangecal/front/advice_model.sav")

# 3. 예측 API 정의
@app.route("/predict", methods=["POST"])
def predict():
    try:
        # 요청 데이터 읽기
        data = request.json
        df = pd.DataFrame([data])

        # 예측 수행
        prediction = model.predict(df)
        return jsonify({"advice": prediction[0]})

    except Exception as e:
        return jsonify({"error": str(e)})

# 4. 서버 실행
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5001)
