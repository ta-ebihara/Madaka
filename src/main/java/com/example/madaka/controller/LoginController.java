package com.example.madaka.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.madaka.common.Const;
import com.example.madaka.form.LoginForm;
import com.example.madaka.repository.DepartmentMaster;
import com.example.madaka.repository.TeamMaster;
import com.example.madaka.repository.TrainMaster;
import com.example.madaka.response.LoginResponse;
import com.example.madaka.service.LoginService;

import jakarta.servlet.http.HttpSession;


@RequestMapping("/madaka")
@Controller
public class LoginController {

	private LoginService loginService;

	public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

	/**
	 * ログイン画面初期表示。
	 *
	 * @param session ログイン情報
	 * @return ログイン画面初期表示
	 *
	 */
	@RequestMapping("/login")
	public String open( Model model,HttpSession session) {
		model.addAttribute("screenName", Const.screenName);
		model.addAttribute("appName", Const.appName);
		session.invalidate();
	    return "login";
	  }

	/**
	 * ログイン処理を行い、認証結果に応じて画面遷移を行う。
	 *
	 * @param model   画面へ値を渡すためのModel
	 * @param form    ログインフォーム用のフォームクラス
	 * @param session ログイン情報
	 * @return メインメニュー、エラー時はログイン画面
	 *
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login( Model model,@ModelAttribute LoginForm form, HttpSession session) {
		loginInfo(form, session);
		if (session.getAttribute("loginUser") == null) {
			model.addAttribute("screenName", Const.screenName);
			model.addAttribute("appName", Const.appName);
			model.addAttribute("empId", form.getEmpId());
			return "login";
		}
		return "redirect:/madaka/menu";
	}

	/**
	 * ログイン情報及びマスタ情報取得
	 *
	 * @param form ログインフォーム用のフォームクラス
	 * @param session ログイン情報
	 * @return ログイン情報、マスタ情報
	 *
	 */
	@ResponseBody
	public  void loginInfo(@ModelAttribute LoginForm form, HttpSession session) {
		LoginResponse response = loginService.login(form);
		List<DepartmentMaster> departmentMaster = loginService.getDepartmentMaster();
		List<TeamMaster> teamMaster = loginService.getTeamMaster();
		List<TrainMaster> trainMaster = loginService.getTrainMaster();

        if (response != null) {
            // ログイン情報をセッションに保持
            session.setAttribute("loginUser", response);

            // マスタ情報もセッションに保持
            session.setAttribute("departments", departmentMaster );
            session.setAttribute("teams", teamMaster );
            session.setAttribute("trains", trainMaster);
        }

    }

}