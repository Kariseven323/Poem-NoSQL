import json
import pymysql
import os

# 数据库连接信息
DB_HOST = '127.0.0.1'
DB_USER = 'root'
DB_PASSWORD = 'cdj123'
DB_NAME = 'poem'

# 连接数据库
def connect_to_db():
    return pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        charset='utf8mb4'
    )

# 创建作者信息的数据库表
def create_author_table():
    try:
        connection = connect_to_db()
        cursor = connection.cursor()

        create_table_sql = """
            CREATE TABLE IF NOT EXISTS authors (
                id VARCHAR(50) PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                headImageUrl VARCHAR(2083),
                simpleIntro TEXT,
                detailIntro JSON
            )
        """

        cursor.execute(create_table_sql)
        connection.commit()
        print("作者表创建成功或已存在！")

    except Exception as e:
        print(f"创建作者表时发生错误: {e}")

    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

# 处理并导入单个 JSON 文件中作者数据到数据库
def import_authors_from_json(json_file_path):
    try:
        # 读取 JSON 文件
        with open(json_file_path, 'r', encoding='utf-8') as file:
            data = file.readlines()

        # 数据库连接
        connection = connect_to_db()
        cursor = connection.cursor()

        # 插入数据的 SQL 语句
        insert_sql = """
            INSERT INTO authors (
                id, name, headImageUrl, simpleIntro, detailIntro
            ) VALUES (%s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE 
                name = VALUES(name),
                headImageUrl = VALUES(headImageUrl),
                simpleIntro = VALUES(simpleIntro),
                detailIntro = VALUES(detailIntro)
        """

        # 遍历每一行，每行是一个独立的 JSON 对象
        for line in data:
            item = json.loads(line.strip())

            author_id = item.get('_id', {}).get('$oid', None)
            name = item.get('name', None)
            head_image_url = item.get('headImageUrl', None)
            simple_intro = item.get('simpleIntro', None)
            detail_intro = item.get('detailIntro', None)

            # 插入数据库
            cursor.execute(insert_sql, (
                author_id, name, head_image_url, simple_intro, detail_intro
            ))

        # 提交事务
        connection.commit()
        print(f"数据从文件 {json_file_path} 成功导入数据库！")

    except Exception as e:
        print(f"处理文件 {json_file_path} 时发生错误: {e}")

    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

# 处理指定目录中的所有 JSON 文件
def import_all_authors_from_directory(directory_path):
    try:
        # 获取目录下所有文件
        json_files = [f for f in os.listdir(directory_path) if f.endswith('.json')]

        for json_file in json_files:
            file_path = os.path.join(directory_path, json_file)
            print(f"正在处理文件: {file_path}")
            import_authors_from_json(file_path)

        print("目录中所有文件已处理完毕！")

    except Exception as e:
        print(f"处理目录 {directory_path} 时发生错误: {e}")

# 调用导入函数
if __name__ == "__main__":
    create_author_table()  # 先创建表
    directory_path = "E:\Project\Poem-NoSQL\db\writer"  # 替换为存放 JSON 文件的目录路径
    import_all_authors_from_directory(directory_path)
