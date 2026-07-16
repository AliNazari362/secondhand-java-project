package Service;

import DTO.adv.*;
import DTO.comment.CommentResponse;
import DTO.image.ImageResponse;
import DTO.option.OptionResponse;
import DTO.user.UserSummaryResponse;
import Entity.*;
import Entity.enums.AdvStatus;
import Entity.enums.AdvType;
import Entity.enums.City;
import Repository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdvService {

    private final AdvRepository advRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final OptionRepository optionRepository;
    private final ImageRepository imageRepository;

    public AdvService(AdvRepository advRepository,
                      ProductRepository productRepository,
                      ServiceRepository serviceRepository,
                      UserService userService,
                      CommentRepository commentRepository,
                      OptionRepository optionRepository,
                      ImageRepository imageRepository) {
        this.advRepository = advRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.optionRepository = optionRepository;
        this.imageRepository = imageRepository;
    }

    // ---------- Create ----------

    public AdvDetailResponse createProduct(ProductCreateRequest request, UUID userId) {
        User user = userService.findUserById(userId);

        Product product = new Product();
        product.setFullName(request.fullName());
        product.setDescription(request.description());
        product.setCity(request.city());
        product.setAddress(request.address());
        product.setUser(user);
        product.setStateOfProduct(request.stateOfProduct());
        product.setBrand(request.brand());
        product.setModel(request.model());
        product.setConstructor(request.constructor());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setAdvType(AdvType.PRODUCT);
        product.setStatus(AdvStatus.PENDING);

        if (request.options() != null) {
            request.options().forEach(opt -> {
                Option option = new Option();  // ✅ سازنده‌ی بدون پارامتر
                option.setOption(opt.option());
                option.setValue(opt.value());
                option.setAdv(product);        // تنظیم رابطه
                product.addOption(option);
            });
        }

        Product saved = productRepository.save(product);
        return toAdvDetailResponse(saved);
    }

    public AdvDetailResponse createService(ServiceCreateRequest request, UUID userId) {
        User user = userService.findUserById(userId);

        Service service = new Service();
        service.setFullName(request.fullName());
        service.setDescription(request.description());
        service.setCity(request.city());
        service.setAddress(request.address());
        service.setUser(user);
        service.setSpecialCategory(request.specialCategory());
        service.setCostOfPart(request.costOfPart());
        service.setTypeOfPart(request.typeOfPart());
        service.setAdvType(AdvType.SERVICE);
        service.setStatus(AdvStatus.PENDING);

        if (request.options() != null) {
            request.options().forEach(opt -> {
                Option option = new Option();  // ✅ سازنده‌ی بدون پارامتر
                option.setOption(opt.option());
                option.setValue(opt.value());
                option.setAdv(service);        // تنظیم رابطه
                service.addOption(option);
            });
        }

        Service saved = serviceRepository.save(service);
        return toAdvDetailResponse(saved);
    }

    // ---------- Read ----------

    public List<AdvSummaryResponse> getAds(String keyword, City city, AdvStatus status) {
        List<Adv> ads = advRepository.search(keyword, city, status);
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<AdvSummaryResponse> getActiveAds(String keyword, City city) {
        return getAds(keyword, city, AdvStatus.ACTIVE);
    }

    public AdvDetailResponse getAdvDetail(UUID advId) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.ACTIVE && adv.getStatus() != AdvStatus.SOLD) {
            throw new RuntimeException("Advertisement is not available");
        }
        return toAdvDetailResponse(adv);
    }

    public List<AdvSummaryResponse> getUserAds(UUID userId) {
        List<Adv> ads = advRepository.findByUserId(userId);
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    // ---------- Update ----------

    public AdvDetailResponse updateProduct(UUID advId, ProductUpdateRequest request, UUID userId) {
        Product product = (Product) findAdvById(advId);
        validateOwnership(product, userId);

        if (request.fullName() != null) product.setFullName(request.fullName());
        if (request.description() != null) product.setDescription(request.description());
        if (request.city() != null) product.setCity(request.city());
        if (request.address() != null) product.setAddress(request.address());
        if (request.stateOfProduct() != null) product.setStateOfProduct(request.stateOfProduct());
        if (request.brand() != null) product.setBrand(request.brand());
        if (request.model() != null) product.setModel(request.model());
        if (request.constructor() != null) product.setConstructor(request.constructor());
        if (request.category() != null) product.setCategory(request.category());
        if (request.price() != null) product.setPrice(request.price());

        if (request.options() != null) {
            optionRepository.deleteByAdvId(advId);
            request.options().forEach(opt -> {
                Option option = new Option();  // ✅ سازنده‌ی بدون پارامتر
                option.setOption(opt.option());
                option.setValue(opt.value());
                option.setAdv(product);        // تنظیم رابطه
                product.addOption(option);
            });
        }

        Product updated = productRepository.save(product);
        return toAdvDetailResponse(updated);
    }

    public AdvDetailResponse updateService(UUID advId, ServiceUpdateRequest request, UUID userId) {
        Service service = (Service) findAdvById(advId);
        validateOwnership(service, userId);

        if (request.fullName() != null) service.setFullName(request.fullName());
        if (request.description() != null) service.setDescription(request.description());
        if (request.city() != null) service.setCity(request.city());
        if (request.address() != null) service.setAddress(request.address());
        if (request.specialCategory() != null) service.setSpecialCategory(request.specialCategory());
        if (request.costOfPart() != null) service.setCostOfPart(request.costOfPart());
        if (request.typeOfPart() != null) service.setTypeOfPart(request.typeOfPart());

        if (request.options() != null) {
            optionRepository.deleteByAdvId(advId);
            request.options().forEach(opt -> {
                Option option = new Option();  // ✅ سازنده‌ی بدون پارامتر
                option.setOption(opt.option());
                option.setValue(opt.value());
                option.setAdv(service);        // تنظیم رابطه
                service.addOption(option);
            });
        }

        Service updated = serviceRepository.save(service);
        return toAdvDetailResponse(updated);
    }

    // ---------- Delete ----------

    public void deleteAdv(UUID advId, UUID userId) {
        Adv adv = findAdvById(advId);
        validateOwnership(adv, userId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    // ---------- Status management ----------

    public void markAsSold(UUID advId, UUID userId) {
        Adv adv = findAdvById(advId);
        validateOwnership(adv, userId);
        if (adv.getStatus() != AdvStatus.ACTIVE) {
            throw new RuntimeException("Only active advertisements can be marked as sold");
        }
        adv.setStatus(AdvStatus.SOLD);
        advRepository.save(adv);
    }

    // ---------- Admin ----------

    public void approveAdv(UUID advId) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.PENDING) {
            throw new RuntimeException("Advertisement is not pending");
        }
        adv.setStatus(AdvStatus.ACTIVE);
        advRepository.save(adv);
    }

    public void rejectAdv(UUID advId, String reason) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.PENDING) {
            throw new RuntimeException("Advertisement is not pending");
        }
        adv.setStatus(AdvStatus.REJECTED);
        adv.setRejectionExplanation(reason);
        advRepository.save(adv);
    }

    public List<AdvSummaryResponse> getPendingAds() {
        List<Adv> ads = advRepository.findByStatus(AdvStatus.PENDING);
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    // ---------- Helpers ----------

    public Adv findAdvById(UUID advId) {
        return advRepository.findById(advId)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));
    }

    private void validateOwnership(Adv adv, UUID userId) {
        if (!adv.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this advertisement");
        }
    }

    // ---------- Mappers ----------

    public AdvSummaryResponse toAdvSummaryResponse(Adv adv) {
        String firstImage = adv.getImages().isEmpty() ? null : adv.getImages().get(0).getPath();
        return new AdvSummaryResponse(
                adv.getId(),
                adv.getFullName(),
                adv.getAdvType(),
                adv.getStatus(),
                adv.getCity(),
                adv.getUser().getFullName(),
                adv.getUser().getId(),
                adv.getCreationDate(),
                firstImage
        );
    }

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

        ProductDetailResponse productDetail = null;
        ServiceDetailResponse serviceDetail = null;

        if (adv.getAdvType() == AdvType.PRODUCT && adv instanceof Product) {
            Product p = (Product) adv;
            productDetail = new ProductDetailResponse(
                    p.getStateOfProduct(),
                    p.getBrand(),
                    p.getModel(),
                    p.getConstructor(),
                    p.getCategory(),
                    p.getPrice()
            );
        } else if (adv.getAdvType() == AdvType.SERVICE && adv instanceof Service) {
            Service s = (Service) adv;
            serviceDetail = new ServiceDetailResponse(
                    s.getSpecialCategory(),
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
                serviceDetail
        );
    }
}