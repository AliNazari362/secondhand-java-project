package com.secondhand.service;

import com.secondhand.dto.adv.*;
import com.secondhand.dto.image.ImageRequest;
import com.secondhand.dto.option.OptionRequest;
import com.secondhand.entity.*;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvServiceTest {

    // ==================== MOCK ها ====================
    @Mock
    private AdvRepository advRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserService userService;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdvService advService;

    // ==================== متغیرهای تست ====================
    private UUID userId;
    private User testUser;
    private Category testCategory;
    private Product testProduct;
    private Service testService;
    private ProductCreateRequest productCreateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setUserType(com.secondhand.entity.enums.UserType.USER);

        testCategory = new Category();
        ReflectionTestUtils.setField(testCategory, "id", 1L);
        testCategory.setName("Electronics");
        testCategory.setType(AdvType.PRODUCT);

        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setFullName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setCity(City.TEHRAN);
        testProduct.setUser(testUser);
        testProduct.setStatus(AdvStatus.ACTIVE);
        testProduct.setAdvType(AdvType.PRODUCT);
        testProduct.setCategory(testCategory);
        testProduct.setPrice(BigDecimal.valueOf(1000000));

        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setFullName("Test Service");
        testService.setUser(testUser);
        testService.setStatus(AdvStatus.ACTIVE);
        testService.setAdvType(AdvType.SERVICE);
        testService.setCategory(testCategory);
        testService.setSpecialCategory("Cleaning");
        testService.setCostOfPart(BigDecimal.valueOf(500000));
        testService.setTypeOfPart(com.secondhand.entity.Service.ServiceType.HOURLY);

        productCreateRequest = new ProductCreateRequest(
                "New Product",
                "Product Description",
                City.TEHRAN,
                "Street 123",
                Product.ProductState.NEW,
                "Samsung",
                "Galaxy S21",
                "Samsung Corp",
                1L,
                BigDecimal.valueOf(2000000),
                List.of(new OptionRequest("RAM", "8GB")),
                List.of(new ImageRequest("uploads/image1.jpg"))
        );
    }

    // ==================== CREATE PRODUCT TESTS ====================

    @Test
    void createProduct_ShouldSucceed_WhenValidRequest() {
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        var response = advService.createProduct(productCreateRequest, userId);

        assertNotNull(response);
        assertEquals(testProduct.getFullName(), response.fullName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenCategoryNotFound() {
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> advService.createProduct(productCreateRequest, userId));
    }

    // ==================== CREATE SERVICE TESTS ====================

    @Test
    void createService_ShouldSucceed_WhenValidRequest() {
        ServiceCreateRequest serviceRequest = new ServiceCreateRequest(
                "New Service",
                "Service Description",
                City.TEHRAN,
                "Street 123",
                "Cleaning",
                1L,
                BigDecimal.valueOf(500000),
                com.secondhand.entity.Service.ServiceType.HOURLY,
                List.of(new OptionRequest("Type", "Standard")),
                List.of(new ImageRequest("uploads/service.jpg"))
        );

        Service testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setFullName("New Service");
        testService.setUser(testUser);
        testService.setStatus(AdvStatus.PENDING);
        testService.setAdvType(AdvType.SERVICE);
        testService.setCategory(testCategory);

        when(userService.findUserById(userId)).thenReturn(testUser);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        var response = advService.createService(serviceRequest, userId);

        assertNotNull(response);
        assertEquals(testService.getFullName(), response.fullName());
        verify(serviceRepository).save(any(Service.class));
    }

    // ==================== SEARCH TESTS (اصلاح‌شده) ====================

    @Test
    void getActiveAds_ShouldReturnFilteredResults() {
        // اصلاح: ارسال ۶ پارامتر به متد search (با minPrice و maxPrice = null)
        when(advRepository.search(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(testProduct));

        var results = advService.getActiveAds("Samsung", City.TEHRAN, 1L, "newest", null, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Product", results.get(0).fullName());
        verify(advRepository).search("Samsung", City.TEHRAN, AdvStatus.ACTIVE, 1L, "newest", null, null);
    }

    @Test
    void getActiveAds_ShouldReturnEmptyList_WhenNoResults() {
        when(advRepository.search(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        var results = advService.getActiveAds("nonexistent", null, null, "newest", null, null);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void getActiveAds_ShouldSortByPriceAsc() {
        when(advRepository.search(any(), any(), any(), any(), eq("priceAsc"), any(), any()))
                .thenReturn(List.of(testProduct));

        var results = advService.getActiveAds(null, null, null, "priceAsc", null, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(advRepository).search(null, null, AdvStatus.ACTIVE, null, "priceAsc", null, null);
    }

    @Test
    void getActiveAds_ShouldSortByRatingDesc() {
        when(advRepository.search(any(), any(), any(), any(), eq("ratingDesc"), any(), any()))
                .thenReturn(List.of(testProduct));

        var results = advService.getActiveAds(null, null, null, "ratingDesc", null, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(advRepository).search(null, null, AdvStatus.ACTIVE, null, "ratingDesc", null, null);
    }

    @Test
    void getActiveAds_ShouldFilterByPriceRange() {
        BigDecimal minPrice = BigDecimal.valueOf(500000);
        BigDecimal maxPrice = BigDecimal.valueOf(2000000);
        when(advRepository.search(any(), any(), any(), any(), any(), eq(minPrice), eq(maxPrice)))
                .thenReturn(List.of(testProduct));

        var results = advService.getActiveAds(null, null, null, "newest", minPrice, maxPrice);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(advRepository).search(null, null, AdvStatus.ACTIVE, null, "newest", minPrice, maxPrice);
    }

    // ==================== DETAIL TESTS ====================

    @Test
    void getAdvDetail_ShouldReturnDetails_WhenActive() {
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        var response = advService.getAdvDetail(testProduct.getId());

        assertNotNull(response);
        assertEquals(testProduct.getFullName(), response.fullName());
        assertEquals(testProduct.getCategory().getName(), response.categoryName());
    }

    @Test
    void getAdvDetail_ShouldThrowException_WhenNotActiveOrSold() {
        testProduct.setStatus(AdvStatus.PENDING);
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(BadRequestException.class,
                () -> advService.getAdvDetail(testProduct.getId()));
    }

    @Test
    void getAdvDetail_ShouldThrowException_WhenNotFound() {
        when(advRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> advService.getAdvDetail(UUID.randomUUID()));
    }

    // ==================== GET USER ADS TESTS ====================

    @Test
    void getUserAds_ShouldReturnList() {
        when(advRepository.findByUserId(userId)).thenReturn(List.of(testProduct));

        var results = advService.getUserAds(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testProduct.getFullName(), results.get(0).fullName());
    }

    @Test
    void getUserAds_ShouldReturnEmptyList_WhenNoAds() {
        when(advRepository.findByUserId(userId)).thenReturn(List.of());

        var results = advService.getUserAds(userId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void updateProduct_ShouldSucceed_WhenOwnershipValid() {
        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                "Updated Product",
                "Updated Description",
                City.ISFAHAN,
                "New Address",
                Product.ProductState.GOOD,
                "Apple",
                "iPhone 13",
                "Apple Inc.",
                1L,
                BigDecimal.valueOf(3000000),
                null
        );

        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        var response = advService.updateProduct(testProduct.getId(), updateRequest, userId);

        assertNotNull(response);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenUserNotOwner() {
        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                "Updated Product", null, null, null, null, null,
                null, null, null, null, null
        );
        UUID otherUserId = UUID.randomUUID();

        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(ForbiddenException.class,
                () -> advService.updateProduct(testProduct.getId(), updateRequest, otherUserId));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenNotProductType() {
        Service service = new Service();
        service.setId(UUID.randomUUID());
        service.setAdvType(AdvType.SERVICE);

        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                "Updated", null, null, null, null, null,
                null, null, null, null, null
        );

        when(advRepository.findById(service.getId())).thenReturn(Optional.of(service));

        assertThrows(BadRequestException.class,
                () -> advService.updateProduct(service.getId(), updateRequest, userId));
    }

    @Test
    void updateService_ShouldSucceed_WhenOwnershipValid() {
        ServiceUpdateRequest updateRequest = new ServiceUpdateRequest(
                "Updated Service",
                "Updated Description",
                City.ISFAHAN,
                "New Address",
                "Premium Cleaning",
                1L,
                BigDecimal.valueOf(600000),
                com.secondhand.entity.Service.ServiceType.FIXED,
                null
        );

        when(advRepository.findById(testService.getId())).thenReturn(Optional.of(testService));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        var response = advService.updateService(testService.getId(), updateRequest, userId);

        assertNotNull(response);
        verify(serviceRepository).save(any(Service.class));
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteAdv_ShouldSetStatusToDeleted_WhenOwner() {
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(advRepository.save(any(Adv.class))).thenReturn(testProduct);

        advService.deleteAdv(testProduct.getId(), userId);

        assertEquals(AdvStatus.DELETED, testProduct.getStatus());
        verify(advRepository).save(testProduct);
    }

    @Test
    void deleteAdv_ShouldThrowException_WhenUserNotOwner() {
        UUID otherUserId = UUID.randomUUID();
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(ForbiddenException.class,
                () -> advService.deleteAdv(testProduct.getId(), otherUserId));
    }

    // ==================== MARK AS SOLD TESTS ====================

    @Test
    void markAsSold_ShouldSucceed_WhenActiveAndOwner() {
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(advRepository.save(any(Adv.class))).thenReturn(testProduct);

        advService.markAsSold(testProduct.getId(), userId);

        assertEquals(AdvStatus.SOLD, testProduct.getStatus());
        verify(advRepository).save(testProduct);
    }

    @Test
    void markAsSold_ShouldThrowException_WhenNotActive() {
        testProduct.setStatus(AdvStatus.PENDING);
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(BadRequestException.class,
                () -> advService.markAsSold(testProduct.getId(), userId));
    }

    // ==================== ADMIN APPROVAL TESTS ====================

    @Test
    void approveAdv_ShouldSetStatusToActive_WhenPending() {
        testProduct.setStatus(AdvStatus.PENDING);
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(advRepository.save(any(Adv.class))).thenReturn(testProduct);

        advService.approveAdv(testProduct.getId());

        assertEquals(AdvStatus.ACTIVE, testProduct.getStatus());
        verify(advRepository).save(testProduct);
    }

    @Test
    void approveAdv_ShouldThrowException_WhenNotPending() {
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(BadRequestException.class,
                () -> advService.approveAdv(testProduct.getId()));
    }

    @Test
    void rejectAdv_ShouldSetStatusToRejected_WhenPending() {
        testProduct.setStatus(AdvStatus.PENDING);
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(advRepository.save(any(Adv.class))).thenReturn(testProduct);

        advService.rejectAdv(testProduct.getId(), "Invalid content");

        assertEquals(AdvStatus.REJECTED, testProduct.getStatus());
        assertEquals("Invalid content", testProduct.getRejectionExplanation());
        verify(advRepository).save(testProduct);
    }

    @Test
    void rejectAdv_ShouldThrowException_WhenNotPending() {
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(BadRequestException.class,
                () -> advService.rejectAdv(testProduct.getId(), "Invalid"));
    }

    // ==================== GET PENDING ADS TESTS ====================

    @Test
    void getPendingAds_ShouldReturnList() {
        when(advRepository.findByStatus(AdvStatus.PENDING)).thenReturn(List.of(testProduct));

        var results = advService.getPendingAds();

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void getPendingAds_ShouldReturnEmptyList_WhenNoPending() {
        when(advRepository.findByStatus(AdvStatus.PENDING)).thenReturn(List.of());

        var results = advService.getPendingAds();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    void findAdvById_ShouldReturnAdv_WhenExists() {
        when(advRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Adv found = advService.findAdvById(testProduct.getId());

        assertNotNull(found);
        assertEquals(testProduct.getId(), found.getId());
    }

    @Test
    void findAdvById_ShouldThrowException_WhenNotFound() {
        when(advRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> advService.findAdvById(UUID.randomUUID()));
    }

    // ==================== DTO CONVERSION TESTS (اصلاح‌شده) ====================

    @Test
    void toAdvSummaryResponse_ShouldIncludeCategoryNameAndPrice() {
        var response = advService.toAdvSummaryResponse(testProduct);

        assertNotNull(response);
        assertEquals(testProduct.getCategory().getName(), response.categoryName());
        assertEquals(testProduct.getPrice(), response.price()); // بررسی فیلد جدید
    }

    @Test
    void toAdvDetailResponse_ShouldIncludeCategoryName() {
        var response = advService.toAdvDetailResponse(testProduct);

        assertNotNull(response);
        assertEquals(testProduct.getCategory().getName(), response.categoryName());
        assertNotNull(response.productDetail());
        assertEquals(testProduct.getPrice(), response.productDetail().price());
    }

    @Test
    void toAdvDetailResponse_ForService_ShouldIncludeCategoryName() {
        var response = advService.toAdvDetailResponse(testService);

        assertNotNull(response);
        assertEquals(testService.getCategory().getName(), response.categoryName());
        assertNotNull(response.serviceDetail());
        assertEquals("Cleaning", response.serviceDetail().specialCategory());
        assertEquals(testService.getCategory().getName(), response.serviceDetail().categoryName());
    }
}