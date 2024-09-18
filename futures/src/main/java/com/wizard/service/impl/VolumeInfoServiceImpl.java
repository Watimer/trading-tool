package com.wizard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizard.mapper.VolumeInfoMapper;
import com.wizard.model.po.VolumeInfoPo;
import com.wizard.service.VolumeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VolumeInfoServiceImpl extends ServiceImpl<VolumeInfoMapper, VolumeInfoPo> implements VolumeInfoService {

    @Override
    public boolean saveBatch(Long logId, List<VolumeInfoPo> volumeInfoPoList) {
        return super.saveBatch(volumeInfoPoList);
    }
}
