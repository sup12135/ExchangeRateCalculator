import pandas as pd
from sqlalchemy import create_engine, Column, Integer, String, Float, Date
from sqlalchemy.orm import sessionmaker, declarative_base

# 데이터베이스 연결 (MySQL 예시)
DATABASE_URL = 'mysql+pymysql://root:merk@localhost:3306/exchange_db'
engine = create_engine(DATABASE_URL)
Session = sessionmaker(bind=engine)
session = Session()

# 파일 경로 지정
file_path = '/Users/songdongjun/Downloads/원:달러.xlsx'

# Excel 파일을 읽어서 데이터프레임으로 로드
excel_data = pd.read_excel(file_path)

# 필요한 데이터만 추출하고 전처리
cleaned_data = excel_data[['일자', '기준환율']].copy()
cleaned_data.dropna(subset=['일자', '기준환율'], inplace=True)
cleaned_data['기준환율'] = cleaned_data['기준환율'].str.replace(',', '').astype(float)
cleaned_data['일자'] = pd.to_datetime(cleaned_data['일자'], format='%Y.%m.%d')
cleaned_data.rename(columns={'일자': 'date', '기준환율': 'rate'}, inplace=True)

# 테이블 매핑 클래스 정의
Base = declarative_base()

class ExchangeRate(Base):
    __tablename__ = 'exchange_rate'

    id = Column(Integer, primary_key=True)
    currency_from = Column(String(10), default='KRW')
    currency_to = Column(String(10), default='USD')
    rate = Column(Float, nullable=False)
    date = Column(Date, nullable=False)

# 데이터를 삽입하는 코드
for _, row in cleaned_data.iterrows():
    exchange_rate = ExchangeRate(
        currency_from='KRW',
        currency_to='USD',
        rate=row['rate'],
        date=row['date']
    )
    session.add(exchange_rate)

# 커밋하여 데이터베이스에 저장
session.commit()
session.close()
