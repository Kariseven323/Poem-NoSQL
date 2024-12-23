import requests

# 设置目标网页的 URL
url = "https://hz.xusenlin.com/#/tang_si"

# 设置请求头
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
}

# 发送 GET 请求
response = requests.get(url, headers=headers)

# 检查请求是否成功
if response.status_code == 200:
    # 获取网页源代码
    html_content = response.text
    
    # 保存源代码为 txt 文件
    with open("page_source.txt", "w", encoding="utf-8") as file:
        file.write(html_content)
    
    print("网页源代码已成功保存为 page_source.txt")
else:
    print("请求失败，状态码:", response.status_code)
