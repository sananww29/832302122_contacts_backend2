package com.example.contacts.service;

import com.example.contacts.dto.ContactRequest;
import com.example.contacts.entity.Contact;
import com.example.contacts.entity.ContactInfo;
import com.example.contacts.repository.ContactRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ContactService {
	private final ContactRepository contactRepository;

	public ContactService(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

	@Transactional(readOnly = true)
	public List<Contact> listAll() {
		List<Contact> contacts = contactRepository.findAll();
		contacts.sort((a, b) -> Long.compare(a.getId(), b.getId()));
		for (int i = 0; i < contacts.size(); i++) {
			contacts.get(i).setDisplayIndex(i + 1);
		}
		return contacts;
	}

	@Transactional(readOnly = true)
	public Contact getById(Long id) {
		return contactRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("联系人不存在: " + id));
	}

	@Transactional
	public Contact create(ContactRequest req) {
		Contact contact = new Contact();
		contact.setName(req.getName());
		contact.setCategory(req.getCategory());
		contact.setFavorite(req.isFavorite());

		// 处理联系方式
		if (req.getContactInfos() != null && !req.getContactInfos().isEmpty()) {
			req.getContactInfos().forEach(infoReq -> {
				ContactInfo info = new ContactInfo();
				info.setContact(contact);
				info.setType(infoReq.getType());
				info.setValue(infoReq.getValue());
				info.setPrimary(infoReq.isPrimary());
				contact.getContactInfos().add(info);
			});
		}

		return contactRepository.save(contact);
	}

	@Transactional
	public Contact update(Long id, ContactRequest req) {
		Contact exist = contactRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("联系人不存在: " + id));
		exist.setName(req.getName());
		exist.setCategory(req.getCategory());
		exist.setFavorite(req.isFavorite());

		// 清空旧的联系方式
		exist.getContactInfos().clear();

		// 添加新的联系方式
		if (req.getContactInfos() != null && !req.getContactInfos().isEmpty()) {
			req.getContactInfos().forEach(infoReq -> {
				ContactInfo info = new ContactInfo();
				info.setContact(exist);
				info.setType(infoReq.getType());
				info.setValue(infoReq.getValue());
				info.setPrimary(infoReq.isPrimary());
				exist.getContactInfos().add(info);
			});
		}

		return contactRepository.save(exist);
	}

	@Transactional
	public void delete(Long id) {
		if (!contactRepository.existsById(id)) {
			throw new IllegalArgumentException("联系人不存在: " + id);
		}
		contactRepository.deleteById(id);
	}

	@Transactional
	public Contact toggleFavorite(Long id) {
		Contact contact = contactRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("联系人不存在: " + id));
		contact.setFavorite(!contact.isFavorite());
		return contactRepository.save(contact);
	}

	// 导出联系人到Excel
	@Transactional(readOnly = true)
	public ByteArrayInputStream exportContactsToExcel() throws IOException {
		String[] headers = {"ID", "姓名", "分类", "是否收藏", "联系方式类型", "联系方式值", "是否主联系方式"};

		// 创建工作簿
		Workbook workbook = new XSSFWorkbook();
		CreationHelper creationHelper = workbook.getCreationHelper();

		// 创建工作表
		Sheet sheet = workbook.createSheet("联系人列表");

		// 创建表头
		Row headerRow = sheet.createRow(0);
		for (int col = 0; col < headers.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(headers[col]);
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			cell.setCellStyle(style);
		}

		// 获取所有联系人
		List<Contact> contacts = contactRepository.findAll();

		// 填充数据
		int rowIdx = 1;
		for (Contact contact : contacts) {
			// 如果联系人没有联系方式，创建一行显示基本信息
			if (contact.getContactInfos() == null || contact.getContactInfos().isEmpty()) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(contact.getId());
				row.createCell(1).setCellValue(contact.getName());
				row.createCell(2).setCellValue(contact.getCategory() != null ? contact.getCategory() : "");
				row.createCell(3).setCellValue(contact.isFavorite() ? "是" : "否");
			} else {
				// 为每个联系方式创建一行
				for (ContactInfo info : contact.getContactInfos()) {
					Row row = sheet.createRow(rowIdx++);
					row.createCell(0).setCellValue(contact.getId());
					row.createCell(1).setCellValue(contact.getName());
					row.createCell(2).setCellValue(contact.getCategory() != null ? contact.getCategory() : "");
					row.createCell(3).setCellValue(contact.isFavorite() ? "是" : "否");
					row.createCell(4).setCellValue(info.getType());
					row.createCell(5).setCellValue(info.getValue());
					row.createCell(6).setCellValue(info.isPrimary() ? "是" : "否");
				}
			}
		}

		// 设置列宽
		for (int col = 0; col < headers.length; col++) {
			sheet.autoSizeColumn(col);
		}

		// 将工作簿转换为字节数组
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	// 从Excel导入联系人
	@Transactional
	public List<Contact> importContactsFromExcel(MultipartFile file) throws IOException {
		List<Contact> importedContacts = new ArrayList<>();

		// 创建工作簿
		Workbook workbook = WorkbookFactory.create(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);

		// 遍历所有行（跳过表头）
		Iterator<Row> rows = sheet.iterator();
		rows.next(); // 跳过表头

		while (rows.hasNext()) {
			Row currentRow = rows.next();

			// 获取单元格数据
			String name = getCellValue(currentRow.getCell(1));
			String category = getCellValue(currentRow.getCell(2));
			boolean isFavorite = "是".equals(getCellValue(currentRow.getCell(3)));
			String contactType = getCellValue(currentRow.getCell(4));
			String contactValue = getCellValue(currentRow.getCell(5));
			boolean isPrimary = "是".equals(getCellValue(currentRow.getCell(6)));

			// 如果姓名和联系方式类型/值为空，跳过此行
			if (name == null || name.trim().isEmpty() || contactType == null || contactType.trim().isEmpty() || contactValue == null || contactValue.trim().isEmpty()) {
				continue;
			}

			// 查找是否已存在相同姓名的联系人
			Contact contact = importedContacts.stream()
					.filter(c -> name.equals(c.getName()))
					.findFirst()
					.orElse(null);

			// 如果联系人不存在，创建新联系人
			if (contact == null) {
				contact = new Contact();
				contact.setName(name);
				contact.setCategory(category);
				contact.setFavorite(isFavorite);
				importedContacts.add(contact);
			}

			// 添加联系方式
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setContact(contact);
			contactInfo.setType(contactType);
			contactInfo.setValue(contactValue);
			contactInfo.setPrimary(isPrimary);
			contact.getContactInfos().add(contactInfo);
		}

		// 保存所有联系人到数据库
		return contactRepository.saveAll(importedContacts);
	}

	// 获取单元格值的辅助方法
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}

		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			default:
				return null;
		}
	}
}


