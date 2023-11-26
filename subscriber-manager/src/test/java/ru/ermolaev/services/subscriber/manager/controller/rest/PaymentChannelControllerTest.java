package ru.ermolaev.services.subscriber.manager.controller.rest;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ermolaev.services.subscriber.manager.rest.PaymentChannelController;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;
import ru.ermolaev.services.subscriber.manager.service.PaymentChannelService;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

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

@DisplayName("Rest контроллер по работе с каналами платежей ")
@Import(PaymentChannelController.class)
@WebMvcTest(PaymentChannelController.class)
class PaymentChannelControllerTest {

	private static final Long PAYMENT_CHANNEL_ID = 1L;

	@MockBean
	private PaymentChannelService paymentChannelService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех каналов платежей")
	void shouldReturnAllPaymentChannels() throws Exception {
		when(paymentChannelService.findAll()).thenReturn(List.of(DummyDataHelper.getDummyPaymentChannelDto(PAYMENT_CHANNEL_ID)));

		mvc.perform(get("/sub/v1/payment-channel").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(PAYMENT_CHANNEL_ID))
				.andExpect(jsonPath("$[0].name").isNotEmpty());

		verify(paymentChannelService, times(1)).findAll();
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть канал платежа по ID")
	void shouldReturnPaymentChannelById() throws Exception {
		when(paymentChannelService.findOneById(PAYMENT_CHANNEL_ID)).thenReturn(DummyDataHelper.getDummyPaymentChannelDto(PAYMENT_CHANNEL_ID));

		mvc.perform(get("/sub/v1/payment-channel/{id}", PAYMENT_CHANNEL_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(PAYMENT_CHANNEL_ID))
				.andExpect(jsonPath("name").isNotEmpty());

		verify(paymentChannelService, times(1)).findOneById(PAYMENT_CHANNEL_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать канал платежа и вернуть 200 статус")
	void shouldCreatePaymentChannelAndReturn200() throws Exception {
		PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto(PAYMENT_CHANNEL_ID);
		Gson gson = new Gson();
		String paymentChannelJson = gson.toJson(dummyPaymentChannelDto);

		when(paymentChannelService.create(dummyPaymentChannelDto)).thenReturn(dummyPaymentChannelDto);

		mvc.perform(post("/sub/v1/payment-channel")
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentChannelJson))
				.andExpect(status().isOk());

		verify(paymentChannelService, times(1)).create(dummyPaymentChannelDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать канал платежа и должен вернуть 403 статус")
	void shouldNotCreatePaymentChannelAndReturn403() throws Exception {
		PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto(PAYMENT_CHANNEL_ID);
		Gson gson = new Gson();
		String paymentChannelJson = gson.toJson(dummyPaymentChannelDto);

		when(paymentChannelService.create(dummyPaymentChannelDto)).thenReturn(dummyPaymentChannelDto);

		mvc.perform(post("/sub/v1/payment-channel")
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentChannelJson))
				.andExpect(status().isForbidden());

		verify(paymentChannelService, times(0)).create(dummyPaymentChannelDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить канал платежа и вернуть 200 статус")
	void shouldUpdatePaymentChannelAndReturn200() throws Exception {
		PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto(PAYMENT_CHANNEL_ID);
		Gson gson = new Gson();
		String paymentChannelJson = gson.toJson(dummyPaymentChannelDto);

		when(paymentChannelService.update(dummyPaymentChannelDto)).thenReturn(dummyPaymentChannelDto);

		mvc.perform(patch("/sub/v1/payment-channel/{id}", PAYMENT_CHANNEL_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentChannelJson))
				.andExpect(status().isOk());

		verify(paymentChannelService, times(1)).update(dummyPaymentChannelDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить канал платежа и должен вернуть 403 статус")
	void shouldNotUpdatePaymentChannelAndReturn403() throws Exception {
		PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto(PAYMENT_CHANNEL_ID);
		Gson gson = new Gson();
		String paymentChannelJson = gson.toJson(dummyPaymentChannelDto);

		when(paymentChannelService.update(dummyPaymentChannelDto)).thenReturn(dummyPaymentChannelDto);

		mvc.perform(patch("/sub/v1/payment-channel/{id}", PAYMENT_CHANNEL_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(paymentChannelJson))
				.andExpect(status().isForbidden());

		verify(paymentChannelService, times(0)).update(dummyPaymentChannelDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить канал платежа и вернуть 200 статус")
	void shouldDeletePaymentChannelAndReturn200() throws Exception {
		doNothing().when(paymentChannelService).deleteById(PAYMENT_CHANNEL_ID);

		mvc.perform(delete("/sub/v1/payment-channel/{id}", PAYMENT_CHANNEL_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(paymentChannelService, times(1)).deleteById(PAYMENT_CHANNEL_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить канал платежа и должен вернуть 403 статус")
	void shouldNotDeletePaymentChannelAndReturn403() throws Exception {
		doNothing().when(paymentChannelService).deleteById(PAYMENT_CHANNEL_ID);

		mvc.perform(delete("/sub/v1/payment-channel/{id}", PAYMENT_CHANNEL_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(paymentChannelService, times(0)).deleteById(PAYMENT_CHANNEL_ID);
	}

}
