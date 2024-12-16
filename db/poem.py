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

# 创建古文表的数据库表
def create_ancient_poetry_table():
    try:
        connection = connect_to_db()
        cursor = connection.cursor()

        create_table_sql = """
            CREATE TABLE IF NOT EXISTS ancient_poetry (
                id VARCHAR(50) PRIMARY KEY, -- 使用VARCHAR存储MongoDB导出的ObjectId格式
                title VARCHAR(255) NOT NULL, -- 古文标题
                dynasty VARCHAR(50), -- 所属朝代
                writer VARCHAR(100), -- 作者
                type TEXT, -- 类型，用TEXT存储多分类信息
                content TEXT NOT NULL, -- 古文原文
                remark TEXT, -- 注释
                translation TEXT, -- 翻译
                shangxi TEXT, -- 赏析
                audioUrl VARCHAR(2083) -- 朗诵音频链接，符合URL长度标准
            )
        """

        cursor.execute(create_table_sql)
        connection.commit()
        print("古文表创建成功或已存在！")

    except Exception as e:
        print(f"创建古文表时发生错误: {e}")

    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

# 处理并导入单个 JSON 文件中独立 JSON 对象的数据到数据库
def import_disjointed_json_to_db(json_file_path):
    try:
        # 读取 JSON 文件
        with open(json_file_path, 'r', encoding='utf-8') as file:
            data = file.readlines()

        # 数据库连接
        connection = connect_to_db()
        cursor = connection.cursor()

        # 插入数据的 SQL 语句
        insert_sql = """
            INSERT INTO ancient_poetry (
                id, title, dynasty, writer, type, content, remark, translation, shangxi, audioUrl
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE 
                title = VALUES(title),
                dynasty = VALUES(dynasty),
                writer = VALUES(writer),
                type = VALUES(type),
                content = VALUES(content),
                remark = VALUES(remark),
                translation = VALUES(translation),
                shangxi = VALUES(shangxi),
                audioUrl = VALUES(audioUrl)
        """

        # 遍历每一行，每行是一个独立的 JSON 对象
        for line in data:
            item = json.loads(line.strip())

            poetry_id = item.get('_id', {}).get('$oid', None)
            title = item.get('title', None)
            dynasty = item.get('dynasty', None)
            writer = item.get('writer', None)
            type_field = ','.join(item.get('type', []))
            content = item.get('content', None)
            remark = item.get('remark', None)
            translation = item.get('translation', None)
            shangxi = item.get('shangxi', None)
            audio_url = item.get('audioUrl', None)

            # 插入数据库
            cursor.execute(insert_sql, (
                poetry_id, title, dynasty, writer, type_field, content, remark, translation, shangxi, audio_url
            ))

        # 提交事务
        connection.commit()
        print(f"数据从文件 {json_file_path} 成功导入数据库！")

    except Exception as e:
        print(f"处理文件 {json_file_path} 时发生错误: {e}")

    finally:
        # 关闭数据库连接
        if cursor:
            cursor.close()
        if connection:
            connection.close()

# 处理指定目录中的所有 JSON 文件
def import_all_json_from_directory(directory_path):
    try:
        # 获取目录下所有文件
        json_files = [f for f in os.listdir(directory_path) if f.endswith('.json')]

        for json_file in json_files:
            file_path = os.path.join(directory_path, json_file)
            print(f"正在处理文件: {file_path}")
            import_disjointed_json_to_db(file_path)

        print("目录中所有文件已处理完毕！")

    except Exception as e:
        print(f"处理目录 {directory_path} 时发生错误: {e}")

# 调用导入函数
if __name__ == "__main__":
    create_ancient_poetry_table()  # 创建古文表
    directory_path = "E:\\Project\\Poem-NoSQL\\db\\guwen"  # 替换为存放 JSON 文件的目录路径
    import_all_json_from_directory(directory_path)
