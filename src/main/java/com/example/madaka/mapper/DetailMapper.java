package com.example.madaka.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.madaka.response.DetailResponse;

@Mapper
public interface DetailMapper {
	// 遅刻IDで遅刻履歴を1件取得
	DetailResponse selectByLateId(@Param("lateId") String lateId);

	// 到着処理（遅刻時間を更新）
	int updateArrival (@Param("lateId") String lateId, @Param("lateMin") long lateMin);
}
