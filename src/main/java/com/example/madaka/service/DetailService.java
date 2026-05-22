package com.example.madaka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.madaka.form.DetailForm;
import com.example.madaka.mapper.DetailMapper;
import com.example.madaka.response.DetailResponse;

/**
 * @author yu-kudo
 * 詳細画面のserviceクラス
 */
@Component
public class DetailService {

	@Autowired
	DetailMapper detailMapper;

	/**
	 * 遅刻IDで遅刻情報を取得
	 * @param id
	 * @return
	 */
	public DetailResponse getLateInfo(String id) {
		return detailMapper.selectByLateId(id);
	}

    /**
     * SearchResponseの値をDetailResponseに詰める
     *
     * @param lateInfo
     * @param detailInfo
     */
    public void mappingDetail(DetailResponse detailInfo, DetailForm detailForm) {
    	detailForm.setDate(detailInfo.getLate_datetime().toLocalDate());
    	detailForm.setLateId(detailInfo.getLate_id());
    	detailForm.setEmpId(detailInfo.getEmp_id());
    	detailForm.setLateReason(detailInfo.getLate_reason());
    	detailForm.setNote(detailInfo.getNote());
    	detailForm.setStartTime(detailInfo.getStart_time());
    	detailForm.setTrainDelayMinutes(detailInfo.getTrain_delay_min());
    	detailForm.setTrainName(detailInfo.getTrain_name());
    }

    /**
     * 到着処理（遅刻時間更新）
     * @param lateId
     * @param lateMin
     * @return true=更新処理成功
     */
    public boolean isArrivedUpdate(String lateId, long lateMin) {
    	boolean updateSuccess = false;
    	int updateCount = detailMapper.updateArrival(lateId, lateMin);

    	if (updateCount <= 1) {
    		updateSuccess = true;
    	}

    	return updateSuccess;
    }
}
