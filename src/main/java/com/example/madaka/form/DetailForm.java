package com.example.madaka.form;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class DetailForm {

	// 遅刻ID
	private String lateId;
	// 日付
	private LocalDate date;
	// 社員ID
	private String empId;
	// 遅刻理由
	private String lateReason;
    // 電車名
    private String trainName;
	// 遅延時間
	private Integer trainDelayMinutes;
	// 始業時間
	private LocalTime startTime;
	// 遅刻時間
	private String lateMin;
	// 備考
	private String note;

}
