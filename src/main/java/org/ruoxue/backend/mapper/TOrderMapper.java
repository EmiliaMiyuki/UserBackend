package org.ruoxue.backend.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.ruoxue.backend.bean.TOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author fengjb
 * @since 2018-08-30
 */
@Mapper
public interface TOrderMapper extends BaseMapper<TOrder> {

    List<TOrder> listOrderByStatus(@Param("uid") Integer uid, @Param("page") Integer page, @Param("size") Integer size, @Param("status") Integer status);

    List<Map<String, Object>> listOrder(@Param("cid") Integer cid, @Param("type") Integer type, @Param("page") Integer page, @Param("size") Integer size, @Param("status") Integer status, @Param("start") Date start, @Param("end") Date end, @Param("uid") Integer uid);

//    通过running查询订单
    @Select("select * from t_order where running = #{running}")
    TOrder getTOrderByRunning(@Param("running") Long running);

//    更改订单状态
    @Update("update t_order set status = #{status} where id = #{id}")
    Integer updateStatus(@Param("status") Integer status, @Param("id") Integer id);

//    查询最后一个订单
    @Select("select * from t_order order by tm_create desc limit 1")
    TOrder queryLastOrder();

}