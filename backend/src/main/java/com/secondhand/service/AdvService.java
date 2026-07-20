package com.secondhand.service;

import com.secondhand.dto.adv.*;
import com.secondhand.dto.comment.CommentResponse;
import com.secondhand.dto.image.ImageResponse;
import com.secondhand.dto.option.OptionRequest;
import com.secondhand.dto.option.OptionResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.*;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceNotFoundException;
import com.secondhand.repository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing advertisements (products and services).
 *
 * <p>This service handles all advertisement-related operations:</p>
 * <ul>
 *   <li>Creating new advertisements (product or service) with images and options</li>
 *   <li>Retrieving advertisements with various filters</li>
 *   <li>Retrieving full advertisement details</li>
 *   <li>Updating and deleting advertisements (with ownership validation)</li>
 *   <li>Marking advertisements as sold</li>
 *   <li>Admin operations like approving and rejecting advertisements</li>
 * </ul>
 *
 * <p><strong>Image Support:</strong> When creating an advertisement, the client can
 * provide a list of image paths (via {@code images} field in the request DTOs).
 * These images are automatically persisted and associated with the advertisement.
 * The images are returned in the advertisement detail responses.</p>
 *
 * <p><strong>Category Support:</strong> Advertisements are now classified using
 * the new {@link Category} entity. The client provides a {@code categoryId} in
 * create/update requests, which is resolved to a {@link Category} instance.</p>
 *
 * <p><strong>Ownership Validation:</strong> All mutating operations enforce that
 * the requesting user is the owner of the advertisement. This is validated using
 * the {@link #validateOwnership(Adv, UUID)} method.</p>
 *
 * @see Adv
 * @see Product
 * @see Service
 * @see Image
 * @see Option
 * @see Category
 */
@org.springframework.stereotype.Service
public class AdvService {

    private final AdvRepository advRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final OptionRepository optionRepository;
    private final CategoryRepository categoryRepository; // <-- وابستگی جدید

    /**
     * Constructs an {@code AdvService} with all required repository and service dependencies.
     *
     * @param advRepository      the repository for base {@link Adv} entities
     * @param productRepository  the repository for {@link Product} advertisements
     * @param serviceRepository  the repository for {@link Service} advertisements
     * @param userService        the service used for user lookups and persistence
     * @param optionRepository   the repository for managing advertisement {@link Option} entities
     * @param categoryRepository the repository for managing {@link Category} entities
     */
    public AdvService(AdvRepository advRepository,
                      ProductRepository productRepository,
                      ServiceRepository serviceRepository,
                      UserService userService,
                      OptionRepository optionRepository,
                      CategoryRepository categoryRepository) { // <-- پارامتر جدید
        this.advRepository = advRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.optionRepository = optionRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Populates the base fields common to all advertisement types on a new {@link Adv} instance.
     *
     * <p>Sets the full name, description, city, address, owner, initial status ({@link AdvStatus#PENDING}),
     * advertisement type, category, and any provided custom options. This method is used internally by
     * {@link #createProduct(ProductCreateRequest, UUID)} and {@link #createService(ServiceCreateRequest, UUID)}.</p>
     *
     * @param adv         the advertisement entity to populate (must not be {@code null})
     * @param fullName    the display name/title of the advertisement
     * @param description a detailed description of the advertised item or service
     * @param city        the city where the advertisement is located
     * @param address     the specific address for the advertisement
     * @param user        the owner of the advertisement
     * @param advType     the type of advertisement ({@link AdvType#PRODUCT} or {@link AdvType#SERVICE})
     * @param categoryId  the ID of the category to assign to this advertisement (can be {@code null})
     * @param options     an optional list of key-value options; may be {@code null}
     * @throws ResourceNotFoundException if {@code categoryId} is provided but no category exists with that ID
     */
    private void createAdv(
            Adv adv, String fullName, String description,
            City city, String address, User user, AdvType advType,
            Long categoryId, // <-- پارامتر جدید
            List<OptionRequest> options) {

        adv.setFullName(fullName);
        adv.setDescription(description);
        adv.setCity(city);
        adv.setAddress(address);
        adv.setUser(user);
        adv.setStatus(AdvStatus.PENDING);
        adv.setAdvType(advType);

        // تنظیم دسته‌بندی اگر categoryId ارسال شده باشد
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("دسته‌بندی با این شناسه یافت نشد"));
            adv.setCategory(category);
        }

        if (options != null) {
            options.forEach(opt -> {
                Option option = new Option();
                option.setOption(opt.option());
                option.setValue(opt.value());
                option.setAdv(adv);
                adv.addOption(option);
            });
        }
    }

    /**
     * Creates and persists a new {@link Product} advertisement.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Finds the user by ID (extracted from the JWT token).</li>
     *   <li>Creates a {@link Product} entity and populates its fields using {@link #createAdv}.</li>
     *   <li>Sets product-specific fields (condition, brand, model, constructor, price).</li>
     *   <li>Processes and attaches any images provided in the request.</li>
     *   <li>Persists the product and returns its full details.</li>
     * </ol>
     *
     * @param request the product creation data including title, description, location,
     *                price, condition, brand, model, constructor, categoryId, options, and images
     * @param userId  the UUID of the authenticated user creating the advertisement
     * @return a {@link AdvDetailResponse} representing the newly created product advertisement
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public AdvDetailResponse createProduct(ProductCreateRequest request, UUID userId) {
        User user = userService.findUserById(userId);
        Product product = new Product();
        createAdv(product,
                request.fullName(), request.description(),
                request.city(), request.address(),
                user, AdvType.PRODUCT,
                request.categoryId(), // <-- ارسال categoryId از درخواست
                request.options());

        product.setStateOfProduct(request.stateOfProduct());
        product.setBrand(request.brand());
        product.setModel(request.model());
        product.setConstructor(request.constructor());
        product.setPrice(request.price());

        if (request.images() != null) {
            request.images().forEach(imgReq -> {
                Image image = new Image(imgReq.path(), product);
                product.addImage(image);
            });
        }

        Product saved = productRepository.save(product);
        return toAdvDetailResponse(saved);
    }

    /**
     * Creates and persists a new {@link Service} advertisement.
     *
     * <p>This method is similar to {@link #createProduct(ProductCreateRequest, UUID)}
     * and populates service-specific fields (specialCategory, costOfPart, typeOfPart).</p>
     *
     * @param request the service creation data including title, description, location,
     *                special category, cost of part, type of part, categoryId, options, and images
     * @param userId  the UUID of the authenticated user creating the advertisement
     * @return a {@link AdvDetailResponse} representing the newly created service advertisement
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public AdvDetailResponse createService(ServiceCreateRequest request, UUID userId) {
        User user = userService.findUserById(userId);
        Service service = new Service();
        createAdv(service,
                request.fullName(), request.description(),
                request.city(), request.address(),
                user, AdvType.SERVICE,
                request.categoryId(), // <-- ارسال categoryId از درخواست
                request.options());

        service.setSpecialCategory(request.specialCategory());
        service.setCostOfPart(request.costOfPart());
        service.setTypeOfPart(request.typeOfPart());

        if (request.images() != null) {
            request.images().forEach(imgReq -> {
                Image image = new Image(imgReq.path(), service);
                service.addImage(image);
            });
        }

        Service saved = serviceRepository.save(service);
        return toAdvDetailResponse(saved);
    }

    /**
     * Searches for advertisements matching the given filters.
     *
     * @param keyword    an optional keyword to match against advertisement titles/descriptions;
     *                   may be {@code null} to skip keyword filtering
     * @param city       an optional city filter; may be {@code null} to include all cities
     * @param status     an optional status filter; may be {@code null} to include all statuses
     * @param categoryId an optional category ID filter; may be {@code null} to include all categories
     * @return a list of {@link AdvSummaryResponse} objects matching the given criteria;
     *         never {@code null}, may be empty
     */
    public List<AdvSummaryResponse> getAds(String keyword, City city, AdvStatus status, Long categoryId) {
        List<Adv> ads = advRepository.search(keyword, city, status, categoryId); // <-- ارسال categoryId به ریپازیتوری
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns all currently active advertisements matching the given filters.
     *
     * <p>This is a convenience method that delegates to {@link #getAds(String, City, AdvStatus, Long)}
     * with {@link AdvStatus#ACTIVE} as the status.</p>
     *
     * @param keyword    an optional keyword filter; may be {@code null}
     * @param city       an optional city filter; may be {@code null}
     * @param categoryId an optional category ID filter; may be {@code null}
     * @return a list of active {@link AdvSummaryResponse} objects; never {@code null}, may be empty
     */
    public List<AdvSummaryResponse> getActiveAds(String keyword, City city, Long categoryId) {
        return getAds(keyword, city, AdvStatus.ACTIVE, categoryId);
    }

    /**
     * Returns the full detail of a publicly accessible advertisement.
     *
     * <p>Only advertisements in {@link AdvStatus#ACTIVE} or {@link AdvStatus#SOLD}
     * are accessible through this endpoint. Other statuses (e.g., PENDING, REJECTED, DELETED)
     * are considered not publicly available.</p>
     *
     * @param advId the UUID of the advertisement to retrieve
     * @return a {@link AdvDetailResponse} for the specified advertisement
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws BadRequestException       if the advertisement exists but is not publicly accessible
     */
    public AdvDetailResponse getAdvDetail(UUID advId) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.ACTIVE && adv.getStatus() != AdvStatus.SOLD) {
            throw new BadRequestException("این آگهی در دسترس عموم نیست");
        }
        return toAdvDetailResponse(adv);
    }

    /**
     * Returns a summary list of all advertisements belonging to a specific user.
     *
     * @param userId the UUID of the user whose advertisements should be retrieved
     * @return a list of {@link AdvSummaryResponse} objects for the given user;
     *         never {@code null}, may be empty
     */
    public List<AdvSummaryResponse> getUserAds(UUID userId) {
        List<Adv> ads = advRepository.findByUserId(userId);
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates the base fields common to all advertisement types on an existing {@link Adv}.
     *
     * <p>Validates that the requesting user owns the advertisement before applying any changes.
     * Only non-{@code null} fields from the parameters are applied. If a new options list
     * is provided, the existing options are deleted and replaced.</p>
     *
     * @param adv         the advertisement entity to update
     * @param fullName    the new title; {@code null} means no change
     * @param description the new description; {@code null} means no change
     * @param city        the new city; {@code null} means no change
     * @param address     the new address; {@code null} means no change
     * @param userId      the UUID of the requesting user (used for ownership validation)
     * @param categoryId  the new category ID; {@code null} means no change
     * @param options     the replacement options list; {@code null} means no change
     * @throws ForbiddenException        if the requesting user does not own the advertisement
     * @throws ResourceNotFoundException if {@code categoryId} is provided but no category exists
     */
    private void updateAdv(
            Adv adv, String fullName, String description,
            City city, String address, UUID userId,
            Long categoryId, // <-- پارامتر جدید
            List<OptionRequest> options) {

        validateOwnership(adv, userId);
        if (fullName != null) adv.setFullName(fullName);
        if (description != null) adv.setDescription(description);
        if (city != null) adv.setCity(city);
        if (address != null) adv.setAddress(address);

        // به‌روزرسانی دسته‌بندی اگر categoryId ارسال شده باشد
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("دسته‌بندی با این شناسه یافت نشد"));
            adv.setCategory(category);
        }

        if (options != null) {
            optionRepository.deleteByAdvId(adv.getId());
            options.forEach(opt -> {
                Option option = new Option();
                option.setOption(opt.option());
                option.setValue(opt.value());
                option.setAdv(adv);
                adv.addOption(option);
            });
        }
    }

    /**
     * Updates an existing {@link Product} advertisement.
     *
     * <p>Verifies that the advertisement identified by {@code advId} is actually a
     * {@link Product} before proceeding. Base fields are updated via {@link #updateAdv},
     * then product-specific fields (condition, brand, model, constructor, price)
     * are applied for any non-{@code null} values in the request.</p>
     *
     * @param advId   the UUID of the advertisement to update
     * @param request the product update data; only non-{@code null} fields are applied
     * @param userId  the UUID of the requesting user
     * @return a {@link AdvDetailResponse} reflecting the updated product advertisement
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws BadRequestException       if the advertisement is not of type {@link Product}
     * @throws ForbiddenException        if the requesting user does not own the advertisement
     */
    public AdvDetailResponse updateProduct(UUID advId, ProductUpdateRequest request, UUID userId) {
        Adv foundAdv = findAdvById(advId);
        if (!(foundAdv instanceof Product)) {
            throw new BadRequestException("این آگهی از نوع محصول نیست");
        }
        Product product = (Product) foundAdv;
        updateAdv(product,
                request.fullName(), request.description(),
                request.city(), request.address(),
                userId,
                request.categoryId(), // <-- ارسال categoryId از درخواست
                request.options());

        if (request.stateOfProduct() != null) product.setStateOfProduct(request.stateOfProduct());
        if (request.brand() != null) product.setBrand(request.brand());
        if (request.model() != null) product.setModel(request.model());
        if (request.constructor() != null) product.setConstructor(request.constructor());
        if (request.price() != null) product.setPrice(request.price());

        Product updated = productRepository.save(product);
        return toAdvDetailResponse(updated);
    }

    /**
     * Updates an existing {@link Service} advertisement.
     *
     * <p>Verifies that the advertisement identified by {@code advId} is actually a
     * {@link Service} before proceeding. Base fields are updated via {@link #updateAdv},
     * then service-specific fields (special category, cost of part, type of part)
     * are applied for any non-{@code null} values in the request.</p>
     *
     * @param advId   the UUID of the advertisement to update
     * @param request the service update data; only non-{@code null} fields are applied
     * @param userId  the UUID of the requesting user
     * @return a {@link AdvDetailResponse} reflecting the updated service advertisement
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws BadRequestException       if the advertisement is not of type {@link Service}
     * @throws ForbiddenException        if the requesting user does not own the advertisement
     */
    public AdvDetailResponse updateService(UUID advId, ServiceUpdateRequest request, UUID userId) {
        Adv foundAdv = findAdvById(advId);
        if (!(foundAdv instanceof Service)) {
            throw new BadRequestException("این آگهی از نوع خدمات نیست");
        }
        Service service = (Service) foundAdv;
        updateAdv(service,
                request.fullName(), request.description(),
                request.city(), request.address(),
                userId,
                request.categoryId(), // <-- ارسال categoryId از درخواست
                request.options());

        if (request.specialCategory() != null) service.setSpecialCategory(request.specialCategory());
        if (request.costOfPart() != null) service.setCostOfPart(request.costOfPart());
        if (request.typeOfPart() != null) service.setTypeOfPart(request.typeOfPart());

        Service updated = serviceRepository.save(service);
        return toAdvDetailResponse(updated);
    }

    /**
     * Soft-deletes an advertisement by setting its status to {@link AdvStatus#DELETED}.
     *
     * <p>Ownership is validated before the status change is applied.</p>
     *
     * @param advId  the UUID of the advertisement to delete
     * @param userId the UUID of the requesting user
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws ForbiddenException        if the requesting user does not own the advertisement
     */
    public void deleteAdv(UUID advId, UUID userId) {
        Adv adv = findAdvById(advId);
        validateOwnership(adv, userId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    /**
     * Marks an active advertisement as sold by setting its status to {@link AdvStatus#SOLD}.
     *
     * <p>Ownership is validated, and the advertisement must currently be in
     * {@link AdvStatus#ACTIVE} status.</p>
     *
     * @param advId  the UUID of the advertisement to mark as sold
     * @param userId the UUID of the requesting user
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws ForbiddenException        if the requesting user does not own the advertisement
     * @throws BadRequestException       if the advertisement is not currently active
     */
    public void markAsSold(UUID advId, UUID userId) {
        Adv adv = findAdvById(advId);
        validateOwnership(adv, userId);
        if (adv.getStatus() != AdvStatus.ACTIVE) {
            throw new BadRequestException("این آگهی فعال نیست و نمی‌توان آن را به فروش رفته علامت زد");
        }
        adv.setStatus(AdvStatus.SOLD);
        advRepository.save(adv);
    }

    /**
     * Approves a pending advertisement by setting its status to {@link AdvStatus#ACTIVE}.
     *
     * <p>This is an admin operation. The advertisement must currently be in
     * {@link AdvStatus#PENDING} status.</p>
     *
     * @param advId the UUID of the advertisement to approve
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws BadRequestException       if the advertisement is not in pending status
     */
    public void approveAdv(UUID advId) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.PENDING) {
            throw new BadRequestException("این آگهی در وضعیت انتظار نیست");
        }
        adv.setStatus(AdvStatus.ACTIVE);
        advRepository.save(adv);
    }

    /**
     * Rejects a pending advertisement by setting its status to {@link AdvStatus#REJECTED}
     * and recording the rejection reason.
     *
     * <p>This is an admin operation. The advertisement must currently be in
     * {@link AdvStatus#PENDING} status.</p>
     *
     * @param advId  the UUID of the advertisement to reject
     * @param reason the reason for rejection, stored on the advertisement entity
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws BadRequestException       if the advertisement is not in pending status
     */
    public void rejectAdv(UUID advId, String reason) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.PENDING) {
            throw new BadRequestException("این آگهی در وضعیت انتظار نیست");
        }
        adv.setStatus(AdvStatus.REJECTED);
        adv.setRejectionExplanation(reason);
        advRepository.save(adv);
    }

    /**
     * Returns a summary list of all advertisements currently in {@link AdvStatus#PENDING}.
     *
     * <p>Intended for use by administrators to review and moderate new submissions.</p>
     *
     * @return a list of {@link AdvSummaryResponse} objects for pending advertisements;
     *         never {@code null}, may be empty
     */
    public List<AdvSummaryResponse> getPendingAds() {
        List<Adv> ads = advRepository.findByStatus(AdvStatus.PENDING);
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Looks up an advertisement by its UUID, throwing if not found.
     *
     * @param advId the UUID of the advertisement to find
     * @return the {@link Adv} entity with the given ID
     * @throws ResourceNotFoundException if no advertisement exists with the given {@code advId}
     */
    public Adv findAdvById(UUID advId) {
        return advRepository.findById(advId)
                .orElseThrow(() -> new ResourceNotFoundException("آگهی یافت نشد"));
    }

    /**
     * Validates that the given user is the owner of the given advertisement.
     *
     * @param adv    the advertisement whose ownership is being checked
     * @param userId the UUID of the user claiming ownership
     * @throws ForbiddenException if the user's ID does not match the advertisement's owner ID
     */
    private void validateOwnership(Adv adv, UUID userId) {
        if (!adv.getUser().getId().equals(userId)) {
            throw new ForbiddenException("اجازه دسترسی به آگهی ندارید");
        }
    }

    /**
     * Converts an {@link Adv} entity to a lightweight {@link AdvSummaryResponse} DTO.
     *
     * <p>The first image path is used as the thumbnail; {@code null} is used if no images exist.
     * The category name is extracted from the associated {@link Category} if present.</p>
     *
     * @param adv the advertisement entity to convert
     * @return a {@link AdvSummaryResponse} with key summary fields populated
     */
    public AdvSummaryResponse toAdvSummaryResponse(Adv adv) {
        String firstImage = adv.getImages().isEmpty() ? null : adv.getImages().get(0).getPath();
        String categoryName = adv.getCategory() != null ? adv.getCategory().getName() : null; // <-- استخراج نام دسته‌بندی

        return new AdvSummaryResponse(
                adv.getId(),
                adv.getFullName(),
                adv.getAdvType(),
                adv.getStatus(),
                adv.getCity(),
                adv.getUser().getFullName(),
                adv.getUser().getId(),
                adv.getCreationDate(),
                firstImage,
                categoryName // <-- فیلد جدید
        );
    }

    /**
     * Converts an {@link Adv} entity to a full {@link AdvDetailResponse} DTO.
     *
     * <p>Includes the owner summary, all images, all options, all comments, and—depending
     * on the advertisement type—either a {@link ProductDetailResponse} or a
     * {@link ServiceDetailResponse} with type-specific fields. The category name
     * is extracted from the associated {@link Category} if present.</p>
     *
     * @param adv the advertisement entity to convert
     * @return a fully populated {@link AdvDetailResponse}
     */
    public AdvDetailResponse toAdvDetailResponse(Adv adv) {
        User owner = adv.getUser();
        UserSummaryResponse ownerSummary = new UserSummaryResponse(
                owner.getId(),
                owner.getFullName(),
                owner.getEmail(),
                owner.getUserType()
        );

        List<ImageResponse> images = adv.getImages().stream()
                .map(img -> new ImageResponse(img.getId(), img.getPath()))
                .collect(Collectors.toList());

        List<OptionResponse> options = adv.getOptions().stream()
                .map(opt -> new OptionResponse(opt.getId(), opt.getOption(), opt.getValue()))
                .collect(Collectors.toList());

        List<CommentResponse> comments = adv.getComments().stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getText(),
                        c.getRate(),
                        new UserSummaryResponse(
                                c.getUser().getId(),
                                c.getUser().getFullName(),
                                c.getUser().getEmail(),
                                c.getUser().getUserType()
                        ),
                        c.getDate()
                ))
                .collect(Collectors.toList());

        String categoryName = adv.getCategory() != null ? adv.getCategory().getName() : null; // <-- استخراج نام دسته‌بندی

        ProductDetailResponse productDetail = null;
        ServiceDetailResponse serviceDetail = null;

        if (adv.getAdvType() == AdvType.PRODUCT && adv instanceof Product p) {
            productDetail = new ProductDetailResponse(
                    p.getStateOfProduct(),
                    p.getBrand(),
                    p.getModel(),
                    p.getConstructor(),
                    categoryName, // <-- ارسال نام دسته‌بندی (از Adv گرفته شده است)
                    p.getPrice()
            );
        } else if (adv.getAdvType() == AdvType.SERVICE && adv instanceof Service s) {
            serviceDetail = new ServiceDetailResponse(
                    s.getSpecialCategory(),
                    categoryName, // <-- فیلد جدید
                    s.getCostOfPart(),
                    s.getTypeOfPart()
            );
        }

        return new AdvDetailResponse(
                adv.getId(),
                adv.getFullName(),
                adv.getAdvType(),
                adv.getStatus(),
                adv.getDescription(),
                adv.getCity(),
                adv.getAddress(),
                ownerSummary,
                adv.getCreationDate(),
                adv.getLastModifiedDate(),
                adv.getRejectionExplanation(),
                images,
                options,
                comments,
                productDetail,
                serviceDetail,
                categoryName // <-- فیلد جدید
        );
    }
}