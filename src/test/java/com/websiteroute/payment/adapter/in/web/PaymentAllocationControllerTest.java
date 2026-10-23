package com.websiteroute.payment.adapter.in.web;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.websiteroute.payment.application.PaymentAllocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentAllocationController.class)
@Import(PaymentAllocationService.class)
class PaymentAllocationControllerTest {
    @Autowired MockMvc mockMvc;

    @Test
    void reducesWeightsAndReturnsProportionalAmounts() throws Exception {
        mockMvc.perform(post("/api/payments/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":50.00,"currency":"USD","weights":[1200,1800]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(50.0)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.weights", contains(2, 3)))
                .andExpect(jsonPath("$.allocations", contains(20.0, 30.0)));
    }

    @Test
    void distributesLeftoverCentsInRecipientOrder() throws Exception {
        mockMvc.perform(post("/api/payments/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":0.05,"currency":"USD","weights":[1,1,1]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allocations", contains(0.02, 0.02, 0.01)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{",
            "{\"amount\":\"invalid\",\"currency\":\"USD\",\"weights\":[1]}",
            "{}",
            "{\"amount\":50,\"currency\":\"USD\",\"weights\":null}",
            "{\"amount\":50,\"currency\":\"USD\",\"weights\":[]}",
            "{\"amount\":50,\"currency\":\"USD\",\"weights\":[null]}",
            "{\"amount\":50,\"currency\":\"USD\",\"weights\":[0,1]}",
            "{\"amount\":50,\"currency\":\"USD\",\"weights\":[-1,2]}",
            "{\"amount\":50,\"currency\":\"USD\",\"weights\":[1.9,2]}",
            "{\"amount\":-1,\"currency\":\"USD\",\"weights\":[1]}",
            "{\"amount\":0.001,\"currency\":\"USD\",\"weights\":[1]}",
            "{\"amount\":1,\"currency\":\"INVALID\",\"weights\":[1]}",
            "{\"amount\":1,\"currency\":\"XXX\",\"weights\":[1]}",
            "{\"currency\":\"USD\",\"weights\":[1]}"
    })
    void rejectsInvalidAllocationRequests(String request) throws Exception {
        mockMvc.perform(post("/api/payments/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
}
