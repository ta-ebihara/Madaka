package com.example.madaka.controller;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.madaka.form.DetailForm;
import com.example.madaka.response.DetailResponse;
import com.example.madaka.service.DetailService;

import jakarta.servlet.http.HttpSession;

/**
 * @author yu-fujii
 *
 */
@Controller
@RequestMapping("/madaka")
public class DetailController {

    @Autowired
    private DetailService detailService;

	@GetMapping("/detail/{id}")
	public String show(@PathVariable String	 id, Model model, HttpSession session){
		DetailForm detailModel = new DetailForm();
		DetailResponse lateInfo = detailService.getLateInfo(id);
		detailService.mappingDetail(lateInfo, detailModel);

		session.setAttribute("detailModel", detailModel);
		model.addAttribute("detailModel", detailModel);
		return "detail";
	}

	/**
	 * 遅刻ボタン押下時処理
	 *
	 * @param lateId
	 * @param startTimeStr
	 * @param model
	 * @param session
	 * @return
	 */
	@PostMapping("/detail")
	public String arrived(@RequestParam String lateId, @RequestParam String startTime, Model model, HttpSession session) {

		DetailForm detailModel = (DetailForm)session.getAttribute("detailModel");



		LocalTime now = LocalTime.now();
		LocalTime startTimeLT = LocalTime.parse(startTime);

		Duration diff = Duration.between(startTimeLT, now);
		long lateMin = diff.toMinutes();

		if (detailService.isArrivedUpdate(lateId, lateMin)) {
			detailModel.setLateMin(String.valueOf(lateMin));
		}

		session.setAttribute("detailModel", detailModel);
		model.addAttribute("detailModel", detailModel);

		return "detail";
	}

}