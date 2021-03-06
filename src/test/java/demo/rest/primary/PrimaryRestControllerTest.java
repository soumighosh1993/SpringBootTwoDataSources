package demo.rest.primary;

import demo.form.primary.PrimaryForm;
import demo.model.primary.PrimaryModel;
import demo.model.primary.builder.PrimaryModelBuilder;
import demo.repository.primary.PrimaryRepository;
import demo.rest.AbstractRestControllerTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PrimaryRestControllerTest extends AbstractRestControllerTest {

	@Autowired
	private PrimaryRepository primaryRepository;

	@Test
	public void getPrimary() throws Exception {
		mockMvc.perform(get("/api/primary"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.pageable").isMap());
	}

	@Test
	public void savePrimaryWithFormErrors() throws Exception {
		PrimaryForm form = new PrimaryForm();

		mockMvc.perform(post("/api/primary")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors.name").exists())
				.andExpect(jsonPath("$.errors.name.message",
						is(messageSource.getMessage("NotEmpty.primaryForm.name", null, LOCALE))));
	}

	@Test
	public void savePrimary() throws Exception {
		PrimaryForm form = new PrimaryForm();
		form.setName("Unique Name!");

		mockMvc.perform(post("/api/primary")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$.data.id").isNumber())
				.andExpect(jsonPath("$.data.name", is(form.getName())));

	}

	@Test
	public void savePrimaryWithExisting() throws Exception {
		PrimaryModel primaryModel = primaryRepository.save(new PrimaryModelBuilder()
				.withName("Existing name!")
				.build());

		PrimaryForm form = new PrimaryForm();
		form.setName(primaryModel.getName());

		mockMvc.perform(post("/api/primary")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors.name").exists())
				.andExpect(jsonPath("$.errors.name.message",
						is(messageSource.getMessage("UniquePrimary.primaryForm.name", null, LOCALE))));
	}

}