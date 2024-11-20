import pandas as pd
from sqlalchemy import create_engine, Column, Integer, String, Float, Date
from sqlalchemy.orm import sessionmaker, declarative_base

# 데이터베이스 연결
DATABASE_URL = 'mysql+pymysql://root:merk@localhost:3306/exchange_db'
engine = create_engine(DATABASE_URL)
Session = sessionmaker(bind=engine)
session = Session()

# 파일 경로 지정
file_path = '/Users/songdongjun/Downloads/위안.xlsx'

# Excel 파일을 읽어서 데이터프레임으로 로드
excel_data = pd.read_excel(file_path)

# 필요한 데이터만 추출하고 전처리
cleaned_data = excel_data[['일자', '기준환율']].copy()
cleaned_data.dropna(subset=['일자', '기준환율'], inplace=True)

# '기준환율' 데이터 타입 확인 및 변환
if cleaned_data['기준환율'].dtype == 'object':  # 문자열일 경우
    cleaned_data['기준환율'] = cleaned_data['기준환율'].str.replace(',', '').astype(float)
else:  # 숫자형일 경우
    cleaned_data['기준환율'] = cleaned_data['기준환율'].astype(float)

cleaned_data['일자'] = pd.to_datetime(cleaned_data['일자'], format='%Y.%m.%d')
cleaned_data.rename(columns={'일자': 'date', '기준환율': 'rate'}, inplace=True)

# 테이블 매핑 클래스 정의
Base = declarative_base()

class WianExchangeRate(Base):
    __tablename__ = 'wonWian'  # 테이블 이름 설정

    id = Column(Integer, primary_key=True)
    currency_from = Column(String(10), default='KRW')  # 기본값 KRW
    currency_to = Column(String(10), default='CNY')  # 기본값 CNY
    rate = Column(Float, nullable=False)
    date = Column(Date, nullable=False)

# 데이터를 삽입하는 코드
for _, row in cleaned_data.iterrows():
    wonWian = WianExchangeRate(
        currency_from='KRW',
        currency_to='CNY',
        rate=row['rate'],
        date=row['date']
    )
    session.add(wonWian)

# 커밋하여 데이터베이스에 저장
session.commit()
session.close()
