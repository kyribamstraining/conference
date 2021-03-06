package com.kyriba.conference.discount.util;

import com.kyriba.conference.discount.api.dto.DiscountDto;
import com.kyriba.conference.discount.api.dto.DiscountResponse;
import com.kyriba.conference.discount.api.dto.DiscountPercentageUpdateDto;
import com.kyriba.conference.discount.domain.DiscountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author Igor Lizura
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountDataMapper {
    DiscountDataMapper MAPPER = Mappers.getMapper( DiscountDataMapper.class );

    @Mapping(source = "type", target = "type")
    DiscountResponse entityToResponse(DiscountEntity entity);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "percentage", target = "percentage")
    DiscountEntity dtoToEntity(DiscountDto discountInputDto);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "percentage", target = "percentage")
    DiscountDto entityToDto(DiscountEntity discountEntity);

    @Mapping(source = "percentage", target = "percentage")
    DiscountEntity paramsToDto(DiscountPercentageUpdateDto parameters);

    void updateDiscountEntityFromParams(DiscountPercentageUpdateDto params, @MappingTarget DiscountEntity discountEntity);
}
