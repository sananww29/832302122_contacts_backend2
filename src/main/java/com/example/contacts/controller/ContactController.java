package com.example.contacts.controller;

import com.example.contacts.dto.ContactRequest;
import com.example.contacts.entity.Contact;
import com.example.contacts.service.ContactService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@Validated
public class ContactController {

	private final ContactService contactService;

	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@GetMapping
	public List<Contact> list() {
		return contactService.listAll();
	}

	@GetMapping("/{id}")
	public Contact detail(@PathVariable Long id) {
		return contactService.getById(id);
	}

	@PostMapping
	public ResponseEntity<Contact> create(@Valid @RequestBody ContactRequest req) {
		Contact created = contactService.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public Contact update(@PathVariable Long id, @Valid @RequestBody ContactRequest req) {
		return contactService.update(id, req);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		contactService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/favorite")
	public Contact toggleFavorite(@PathVariable Long id) {
		return contactService.toggleFavorite(id);
	}

	// 导出联系人到Excel
	@GetMapping("/export")
	public ResponseEntity<InputStreamResource> exportContacts() throws IOException {
		ByteArrayInputStream in = contactService.exportContactsToExcel();

		// 设置响应头
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=contacts.xlsx");

		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(in));
	}

	// 从Excel导入联系人
	@PostMapping("/import")
	public ResponseEntity<List<Contact>> importContacts(@RequestParam("file") MultipartFile file) throws IOException {
		List<Contact> importedContacts = contactService.importContactsFromExcel(file);
		return ResponseEntity.status(HttpStatus.CREATED).body(importedContacts);
	}
}


