import mysql.connector
from mysql.connector import Error
import os
from dotenv import load_dotenv

load_dotenv()


def get_connection():

    try:
        conn = mysql.connector.connect(
            host=os.getenv("DB_HOST", "localhost"),
            port=int(os.getenv("DB_PORT", 3306)),
            user=os.getenv("DB_USER", "root"),
            password=os.getenv("DB_PASSWORD", ""),
            database=os.getenv("DB_NAME", "cinetracker"),
            charset="utf8mb4",
            use_pure=True
        )
        return conn
    except Error as e:
        print(f"[DB ERROR] Não foi possível conectar ao MySQL: {e}")
        raise


def query(sql: str, params: tuple = None) -> list[dict]:

    conn = get_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute(sql, params or ())
        result = cursor.fetchall()
        return result
    finally:
        cursor.close()
        conn.close()


def execute(sql: str, params: tuple = None):

    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(sql, params or ())
        conn.commit()
        return cursor.rowcount
    finally:
        cursor.close()
        conn.close()