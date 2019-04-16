package com.hctym.dao;


import org.springframework.stereotype.Component;

import com.hctym.utils.db.DS;
import com.hctym.utils.db.DynamicDataSource;


@Component
@DynamicDataSource(DS.DB_MANAGE)
public interface TBAdChannelDao {


}
