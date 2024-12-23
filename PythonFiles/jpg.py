import cv2
import numpy as np
import os

# 读取图片
image_path = os.path.abspath(r"D:\项目\Poem-NoSQL\PythonFiles\VCG211324156765.jpg")
image = cv2.imdecode(np.fromfile(image_path, dtype=np.uint8), cv2.IMREAD_COLOR)

if image is None:
    print(f"无法读取图片: {image_path}")
    exit()

# 获取图像的高度和宽度
height, width, _ = image.shape

# 设置水印区域（假设水印在左下角）
watermark_height = 80  # 水印区域的高度
watermark_width = 250  # 水印区域的宽度

# 定义水印区域（左下角）
watermark_region = image[height-watermark_height:height, 0:watermark_width]

# 创建掩码
mask = np.zeros((height, width), dtype=np.uint8)
mask[height-watermark_height:height, 0:watermark_width] = 255

# 使用 inpainting 技术来修复水印区域
image_without_watermark = cv2.inpaint(image, mask, 3, cv2.INPAINT_TELEA)

# 直接覆盖原图片
cv2.imencode('.jpg', image_without_watermark)[1].tofile(image_path)

# 显示处理后的图片
cv2.imshow('Image without Watermark', image_without_watermark)
cv2.waitKey(0)
cv2.destroyAllWindows()
