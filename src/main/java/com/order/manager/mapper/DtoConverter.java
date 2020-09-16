package com.order.manager.mapper;

import com.order.manager.dto.orderLine.AddOrderLineRequest;
import com.order.manager.model.OrderLine;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DtoConverter {

    private final MapperFacade mapper;

    public DtoConverter() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(AddOrderLineRequest.class, OrderLine.class);
        this.mapper = mapperFactory.getMapperFacade();
    }

    public <F,T> T convert(final F from, final Class<T> toClass){
        return this.mapper.map(from, toClass);
    }

    public <F,T> List<T> convert(final List<F> from, final Class<T> toClass){
        List<T> target = new ArrayList<>();
        from.forEach(f -> target.add(this.mapper.map(f,toClass)));
        return target;
    }

    public <F,T> Page<T> convert(final Page<F> from, final Class<T> toClass, Pageable pageable){
        List<T> target = new ArrayList<>();
        from.forEach(f -> target.add(this.mapper.map(f,toClass)));
        return new PageImpl<>(target,pageable,target.size());
    }
}
