package com.example.lab5.web.controller

import com.example.lab5.application.service.OrderService
import com.example.lab5.domain.model.OrderStatus
import com.example.lab5.infrastructure.jpa.entity.UserEntity
import com.example.lab5.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
@Validated
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun listOrders(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?
    ): ResponseEntity<List<OrderResponse>> =
        ResponseEntity.ok(orderService.findAll(userId, status).map { it.toResponse() })

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    fun getOrderById(
        @PathVariable id: Long,
        @AuthenticationPrincipal currentUser: UserEntity
    ): ResponseEntity<OrderResponse> {
        val order = orderService.findById(id, currentUser.id, currentUser.role)
        return ResponseEntity.ok(order.toResponse())
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    fun createOrder(
        @Valid @RequestBody request: OrderCreateRequest,
        @AuthenticationPrincipal currentUser: UserEntity
    ): ResponseEntity<OrderResponse> {
        val order = orderService.create(
            userId = currentUser.id,
            dishIds = request.dishIds ?: emptyList()
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(order.toResponse())
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Void> {
        orderService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestBody request: OrderStatusUpdateRequest
    ): ResponseEntity<OrderResponse> {
        val status = request.status
            ?: throw IllegalArgumentException("Status is required")
        val updated = orderService.updateStatus(id, status)
        return ResponseEntity.ok(updated.toResponse())
    }
}
