<%@ page language="java"  contentType="text/html; charset=UTF-8" %>

<html>
<body>
<h2>tomcat1</h2>
<h2>tomcat1</h2>
<h2>tomcat1 </h2>
<h2>Hello World!</h2>


<form name="login" action="/mail_war/manage/user/login.do" method="post">
    <input type="text" name="username">
    <input type="password" name="password">
    <input type="submit" name="登陆">
</form>

springmvc上传文件
<form name="form1" action="/mail_war/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc上传文件" />
</form>


富文本图片上传文件
<form name="form2" action="/mail_war/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片上传文件" />
</form>

</body>
</html>
