package com.boen.service;


import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.boen.ExceptionHandler.GuliException;
import com.boen.commonUtils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class VodeServiceImpl implements Vodservice{


    @Value("${aliyun.vod.file.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.vod.file.accessKeySecret}")
    private String accessKeySecret;


    @Override
    public String  upload(MultipartFile file) {


        try {
            InputStream inputStream = file.getInputStream();


            String fileName = file.getOriginalFilename();
            System.out.println(fileName);

            String title = fileName.substring(0, fileName.lastIndexOf("."));

            UploadStreamRequest request = new UploadStreamRequest(accessKeyId, accessKeySecret, title, fileName, inputStream);
        /* 是否使用默认水印(可选)，指定模板组ID时，根据模板组配置确定是否使用默认水印*/
        //request.setShowWaterMark(true);
        /* 自定义消息回调设置，参数说明参考文档 https://help.aliyun.com/document_detail/86952.html#UserData */
        //request.setUserData(""{\"Extend\":{\"test\":\"www\",\"localId\":\"xxxx\"},\"MessageCallback\":{\"CallbackURL\":\"http://test.test.com\"}}"");
        /* 视频分类ID(可选) */
        //request.setCateId(0);
        /* 视频标签,多个用逗号分隔(可选) */
        //request.setTags("标签1,标签2");
        /* 视频描述(可选) */
        //request.setDescription("视频描述");
        /* 封面图片(可选) */
        //request.setCoverURL("http://cover.sample.com/sample.jpg");
        /* 模板组ID(可选) */
        //request.setTemplateGroupId("8c4792cbc8694e7084fd5330e56a33d");
        /* 工作流ID(可选) */
        //request.setWorkflowId("d4430d07361f0*be1339577859b0177b");
        /* 存储区域(可选) */
        //request.setStorageLocation("in-201703232118266-5sejdln9o.oss-cn-shanghai.aliyuncs.com");
        /* 开启默认上传进度回调 */
        // request.setPrintProgress(true);
        /* 设置自定义上传进度回调 (必须继承 VoDProgressListener) */
        // request.setProgressListener(new PutObjectProgressListener());
        /* 设置应用ID*/
        //request.setAppId("app-1000000");
        /* 点播服务接入点 */
        //request.setApiRegionId("cn-shanghai");
        /* ECS部署区域*/
        // request.setEcsRegionId("cn-shanghai");
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
        if (response.isSuccess()) {
            System.out.print("VideoId=" + response.getVideoId() + "\n");
        } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            System.out.print("VideoId=" + response.getVideoId() + "\n");
            System.out.print("ErrorCode=" + response.getCode() + "\n");
            System.out.print("ErrorMessage=" + response.getMessage() + "\n");
        }

        return  response.getVideoId();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void deleteVideo(String videoId) {
        try{
            DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", accessKeyId, accessKeySecret);

            DefaultAcsClient client = new DefaultAcsClient(profile);

            DeleteVideoRequest request = new DeleteVideoRequest();

            request.setVideoIds(videoId);

            DeleteVideoResponse response = client.getAcsResponse(request);

            System.out.print("RequestId = " + response.getRequestId() + "\n");

        }catch (Exception e){
            throw new GuliException(20001, "视频删除失败");
        }
    }

    @Override
    public void deleteVideoList(List videoList) {

        try{
            DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", accessKeyId, accessKeySecret);

            DefaultAcsClient client = new DefaultAcsClient(profile);

            DeleteVideoRequest request = new DeleteVideoRequest();

            //传一个字符串进去，id用","分隔
            String str = org.apache.commons.lang.StringUtils.join(videoList.toArray(), ",");

            request.setVideoIds(str);

            DeleteVideoResponse response = client.getAcsResponse(request);

            System.out.print("RequestId = " + response.getRequestId() + "\n");

        }catch (Exception e){
            throw new GuliException(20001, "视频删除失败");
        }


    }
}
