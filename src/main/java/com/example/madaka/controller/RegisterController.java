package com.example.madaka.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.madaka.form.RegisterForm;
import com.example.madaka.repository.EmployeeMaster;
import com.example.madaka.repository.TeamMaster;
import com.example.madaka.repository.TrainMaster;
import com.example.madaka.response.LoginResponse;
import com.example.madaka.response.RegisterResponse;
import com.example.madaka.service.LoginService;
import com.example.madaka.service.RegisterService;

import jakarta.servlet.http.HttpSession;

/**
 * @author yu-fujii
 *
 */
@Controller
@RequestMapping("/madaka")
public class RegisterController {
	private static final String ROLE_GENERAL_EMPLOYEE = "1";
	private static final String ROLE_CHIEF = "2";
	private static final String ROLE_LEADER = "3";
	private static final String ROLE_ASSISTANT_MANAGER = "4";
	private static final String ROLE_MANAGER = "5";

	@Autowired
	private RegisterService registerService;

	@Autowired
	private LoginService loginService;

	//モデル初期化
	@ModelAttribute("registerModel")
	public RegisterResponse registerModel() {
		return new RegisterResponse();
	}

	// 登録画面初期表示
	@GetMapping("/register")
	public String show(@ModelAttribute("registerModel") RegisterResponse registerModel,
			Model model,
			HttpSession session) {
		prepareEmployList(model, session);

		return "register";
	}

	// 登録ボタン押下時の処理
	@PostMapping("/register")
	public String register(RegisterForm form,
			Model model,
			HttpSession session) {
		// セッションからログイン情報（社員ID）を取得
		LoginResponse loginUser = (LoginResponse) session.getAttribute("loginUser");
		if (loginUser == null) {
			model.addAttribute("errorMessage", "ログイン情報が見つかりません。");
			return "register";
		}

		// 登録処理を実行
		String lateId = registerService.register(form, loginUser.getEmpId());

		// 処理成功時は詳細画面へリダイレクト
		return "redirect:/madaka/detail/" + lateId;
	}

	// 社員プルダウン制御を適用
	private void prepareEmployList(Model model, HttpSession session) {
		List<TrainMaster> trainMaster = loginService.getTrainMaster();
		if (trainMaster != null) {
			session.setAttribute("trains", trainMaster);
		}

		List<EmployeeMaster> employeeMasterRaw = loginService.getEmployeeMaster();
		final List<EmployeeMaster> employeeMaster = employeeMasterRaw == null ? List.of() : employeeMasterRaw;

		LoginResponse loginUser = (LoginResponse) session.getAttribute("loginUser");
		boolean disableEmpSelect = false;
		List<EmployeeMaster> filteredEmployeeList = employeeMaster;
		if (loginUser != null) {
			model.addAttribute("loginEmpId", loginUser.getEmpId());

			Optional<EmployeeMaster> loginEmployee = employeeMaster.stream()
					.filter(emp -> emp.getEmpId() != null && emp.getEmpId().equals(loginUser.getEmpId()))
					.findFirst();

			filteredEmployeeList = loginEmployee.map(emp -> {
				String role = emp.getRole();
				if (ROLE_GENERAL_EMPLOYEE.equals(role)) {
					return employeeMaster.stream()
							.filter(e -> e.getEmpId() != null && e.getEmpId().equals(emp.getEmpId()))
							.collect(Collectors.toList());
				}
				if (ROLE_CHIEF.equals(role) || ROLE_LEADER.equals(role)) {
					return employeeMaster.stream()
							.filter(e -> e.getTeamId() != null && e.getTeamId().equals(emp.getTeamId()))
							.filter(e -> "1".equals(e.getBelong()))
							.filter(e -> e.getChangeDate() == null || !"9".equals(e.getChangeDate()))
							.collect(Collectors.toList());
				}
				if (ROLE_ASSISTANT_MANAGER.equals(role) || ROLE_MANAGER.equals(role)) {
					List<TeamMaster> teamMasterRaw = loginService.getTeamMaster();
					List<TeamMaster> teamMaster = teamMasterRaw == null ? List.of() : teamMasterRaw;

					Optional<String> loginUnitNo = teamMaster.stream()
							.filter(team -> team.getTeamId() != null && team.getTeamId().equals(emp.getTeamId()))
							.map(TeamMaster::getUnitNo)
							.filter(unitNo -> unitNo != null && !unitNo.isBlank())
							.findFirst();

					if (loginUnitNo.isEmpty()) {
						return employeeMaster.stream()
								.filter(e -> e.getTeamId() != null && e.getTeamId().equals(emp.getTeamId()))
								.filter(e -> "1".equals(e.getBelong()))
								.filter(e -> e.getEmpStatus() == null || !"9".equals(e.getEmpStatus()))
								.collect(Collectors.toList());
					}

					Set<String> unitTeamIds = teamMaster.stream()
							.filter(team -> loginUnitNo.get().equals(team.getUnitNo()))
							.map(TeamMaster::getTeamId)
							.filter(teamId -> teamId != null && !teamId.isBlank())
							.collect(Collectors.toSet());

					return employeeMaster.stream()
							.filter(e -> e.getTeamId() != null && unitTeamIds.contains(e.getTeamId()))
							.filter(e -> "1".equals(e.getBelong()))
							.filter(e -> e.getEmpStatus() == null || !"9".equals(e.getEmpStatus()))
							.collect(Collectors.toList());
				}
				return employeeMaster;
			}).orElse(employeeMaster);

			disableEmpSelect = loginEmployee
					.map(EmployeeMaster::getRole)
					.map(ROLE_GENERAL_EMPLOYEE::equals)
					.orElse(false);
		}

		model.addAttribute("disableEmpSelect", disableEmpSelect);
		model.addAttribute("employeeList", filteredEmployeeList);
	}
}
