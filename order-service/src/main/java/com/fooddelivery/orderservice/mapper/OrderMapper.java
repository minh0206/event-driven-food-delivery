package com.fooddelivery.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fooddelivery.orderservice.dto.CustomerOrderDto;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderItem;
import com.fooddelivery.shared.dto.DriverOrderDto;
import com.fooddelivery.shared.dto.MasterOrderDto;
import com.fooddelivery.shared.event.OrderItemDetails;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    CustomerOrderDto toCustomerOrderDto(Order order);

    DriverOrderDto toDriverOrderDto(Order order);

    @Mapping(target = "orderItemId", source = "id")
    OrderItemDetails toOrderItemDetails(OrderItem orderItem);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "finalStatus", source = "status")
    @Mapping(target = "deliveryTimestamp", source = "updatedAt")
    MasterOrderDto toMasterOrderDto(Order order);
}
