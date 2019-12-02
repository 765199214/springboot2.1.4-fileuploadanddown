package cn.linkpower.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@RequestMapping("/")
	public String toFileUpload(){
		return "file";
	}
	
	@RequestMapping("/tomore")
	public String toMoreFileUpload(){
		return "morefile";
	}
}
