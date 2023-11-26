package ru.ermolaev.services.subscriber.manager.controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ermolaev.services.subscriber.manager.rest.PaymentController;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;
import ru.ermolaev.services.subscriber.manager.service.PaymentService;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;
import ru.ermolaev.services.subscriber.manager.util.GsonLocalDateAdapter;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Rest контроллер по работе с платежами ")
@Import(PaymentController.class)
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

	private static final Long CHARGE_ID = 1L;

	private static final Long SUBSCRIBER_ID = 1L;

	@MockBean
	private PaymentService paymentService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех платежей для абонента")
	void shouldReturnAllPaymentsForSubscriber() throws Exception {
		when(paymentService.findAllBySubscriberId(SUBSCRIBER_ID)).thenReturn(List.of(DummyDataHelper.getDummyPaymentDto(SUBSCRIBER_ID)));

		mvc.perform(get("/sub/v1/payment/subscriber/{id}", SUBSCRIBER_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(CHARGE_ID))
				.andExpect(jsonPath("$[0].paymentDate").isNotEmpty())
				.andExpect(jsonPath("$[0].amount").isNotEmpty())
				.andExpect(jsonPath("$[0].period").isNotEmpty())
				.andExpect(jsonPath("$[0].comment").isNotEmpty());

		verify(paymentService, times(1)).findAllBySubscriberId(SUBSCRIBER_ID);
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть платеж по ID")
	void shouldReturnPaymentById() throws Exception {
		when(paymentService.findOneById(CHARGE_ID)).thenReturn(DummyDataHelper.getDummyPaymentDto(CHARGE_ID));

		mvc.perform(get("/sub/v1/payment/{id}", CHARGE_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(CHARGE_ID))
				.andExpect(jsonPath("paymentDate").isNotEmpty())
				.andExpect(jsonPath("amount").isNotEmpty())
				.andExpect(jsonPath("period").isNotEmpty())
				.andExpect(jsonPath("comment").isNotEmpty());

		verify(paymentService, times(1)).findOneById(CHARGE_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать платеж и вернуть 200 статус")
	void shouldCreatePaymentAndReturn200() throws Exception {
		PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto(CHARGE_ID);
		dummyPaymentDto.setPaymentChannel(DummyDataHelper.getDummyPaymentChannelDto());
		dummyPaymentDto.setSubscriberId(SUBSCRIBER_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String paymentJson = gson.toJson(dummyPaymentDto);

		when(paymentService.create(dummyPaymentDto)).thenReturn(dummyPaymentDto);

		mvc.perform(post("/sub/v1/payment")
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentJson))
				.andExpect(status().isOk());

		verify(paymentService, times(1)).create(dummyPaymentDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать платеж и должен вернуть 403 статус")
	void shouldNotCreatePaymentAndReturn403() throws Exception {
		PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto(CHARGE_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String paymentJson = gson.toJson(dummyPaymentDto);

		when(paymentService.create(dummyPaymentDto)).thenReturn(dummyPaymentDto);

		mvc.perform(post("/sub/v1/payment")
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentJson))
				.andExpect(status().isForbidden());

		verify(paymentService, times(0)).create(dummyPaymentDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить платеж и вернуть 200 статус")
	void shouldUpdatePaymentAndReturn200() throws Exception {
		PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto(CHARGE_ID);
		dummyPaymentDto.setPaymentChannel(DummyDataHelper.getDummyPaymentChannelDto());
		dummyPaymentDto.setSubscriberId(SUBSCRIBER_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String paymentJson = gson.toJson(dummyPaymentDto);

		when(paymentService.update(dummyPaymentDto)).thenReturn(dummyPaymentDto);

		mvc.perform(patch("/sub/v1/payment/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentJson))
				.andExpect(status().isOk());

		verify(paymentService, times(1)).update(dummyPaymentDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить платеж и должен вернуть 403 статус")
	void shouldNotUpdatePaymentAndReturn403() throws Exception {
		PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto(CHARGE_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String paymentJson = gson.toJson(dummyPaymentDto);

		when(paymentService.update(dummyPaymentDto)).thenReturn(dummyPaymentDto);

		mvc.perform(patch("/sub/v1/payment/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentJson))
				.andExpect(status().isForbidden());

		verify(paymentService, times(0)).update(dummyPaymentDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить платеж и вернуть 200 статус")
	void shouldDeletePaymentAndReturn200() throws Exception {
		doNothing().when(paymentService).deleteById(CHARGE_ID);

		mvc.perform(delete("/sub/v1/payment/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(paymentService, times(1)).deleteById(CHARGE_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить платеж и должен вернуть 403 статус")
	void shouldNotDeletePaymentAndReturn403() throws Exception {
		doNothing().when(paymentService).deleteById(CHARGE_ID);

		mvc.perform(delete("/sub/v1/payment/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(paymentService, times(0)).deleteById(CHARGE_ID);
	}

}
