package cn.linkpower.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.stream.FileImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping("/file")
public class FileController {
	private static Logger log = LoggerFactory.getLogger(FileController.class);
	
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(MultipartFile file,HttpServletRequest request){
		if(request!=null){
			log.info("文件名称-->"+file.getName());
			log.info("文件类型-->"+file.getContentType());
			log.info("文件大小-->"+file.getSize());
			
			//选中代码-->alt + shift + M -->命名即可
			return fileUploadTool(file);
			
		}
		return "failed";
	}

	private String fileUploadTool(MultipartFile file) {
		//获取项目地址
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()
			+"static"+File.separator+"file"+File.separator;
		String filename = UUID.randomUUID().toString().trim().replaceAll("-", "")
				+"-"+file.getOriginalFilename();
		log.info("--->"+path+filename);
		File flFile = new File(path+filename);
		//判断文件路径是否存在
		if(!flFile.getParentFile().exists()){
			flFile.getParentFile().mkdirs();
		}
		//保存操作
		try {
			file.transferTo(flFile);
			return "success";
		} catch (IllegalStateException | IOException e) {
			//e.printStackTrace();
			return "exception";
		}
	}
	
	@RequestMapping("/uploadmore")
	@ResponseBody
	public String uploadmore(HttpServletRequest request){
		//判断请求，是否是文件流请求
		if(request instanceof MultipartHttpServletRequest){
			MultipartHttpServletRequest mlRequest = (MultipartHttpServletRequest) request;
			//根据name属性，获取文件集合
			List<MultipartFile> fileLists = mlRequest.getFiles("file");
			boolean isSuccess = true;
			for (MultipartFile multipartFile : fileLists) {
				String status = fileUploadTool(multipartFile);
				if("failed".equalsIgnoreCase(status)){
					isSuccess = false;
					break;
				}
			}
			if(isSuccess){
				return "success";
			}
		}
		return "failed";
	}
	
	@RequestMapping("/down")
	public void downFile(HttpServletRequest request,
			HttpServletResponse response){
		String fileName = request.getParameter("file");
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()
				+"static"+File.separator+"file"+File.separator;
		File file = new File(path+fileName);
		try {
			FileInputStream inputStream = new FileInputStream(file);
			// 设置相关格式
			response.setContentType("application/force-download");
			// 设置下载后的文件名以及header
			response.addHeader("Content-disposition", "attachment;fileName=" + fileName);
			// 创建输出对象
			OutputStream os = response.getOutputStream();
			// 常规操作
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = inputStream.read(buf)) != -1) {
				os.write(buf, 0, len);
			}
			os.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
