package com.wizard.service;

import com.wizard.model.po.VolumeInfoPo;

import java.util.List;

public interface VolumeInfoService {

    boolean saveBatch(Long logId, List<VolumeInfoPo> volumeInfoPoList);
}
