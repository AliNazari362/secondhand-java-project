package Service;

import DTO.adv.*;
import DTO.comment.CommentResponse;
import DTO.image.ImageResponse;
import DTO.option.OptionRequest;
import DTO.option.OptionResponse;
import DTO.user.UserSummaryResponse;
import Entity.*;
import Entity.enums.AdvStatus;
import Entity.enums.AdvType;
import Entity.enums.City;
import Repository.*;
import SpecialException.AdvertisementIsAlreadySoldException;
import SpecialException.AdvertisementIsNotAvailableException;
import SpecialException.IllegalOwnershipException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdvService {

    private final AdvRepository advRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final OptionRepository optionRepository;

    public AdvService(AdvRepository advRepository,
                      ProductRepository productRepository,
                      ServiceRepository serviceRepository,
                      UserService userService,
                      OptionRepository optionRepository) {
        this.advRepository = advRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.optionRepository = optionRepository;
    }

    private void createAdv(
            Adv adv, String fullName, String description,
            City city, String address, User user, AdvType avdType,
            List<OptionRequest> options) {

        adv.setFullName(fullName);
        adv.setDescription(description);
        adv.setCity(city);
        adv.setAddress(address);
        adv.setUser(user);
        adv.setStatus(AdvStatus.PENDING);
        adv.setAdvType(avdType);

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

    public AdvDetailResponse createProduct(ProductCreateRequest request, UUID userId) {
        User user = userService.findUserById(userId);

        Product product = new Product();

        createAdv(product,
                request.fullName(), request.description(),
                request.city(), request.address(),
                user, AdvType.PRODUCT, request.options());

        product.setStateOfProduct(request.stateOfProduct());
        product.setBrand(request.brand());
        product.setModel(request.model());
        product.setConstructor(request.constructor());
        product.setCategory(request.category());
        product.setPrice(request.price());

        Product saved = productRepository.save(product);
        return toAdvDetailResponse(saved);
    }

    public AdvDetailResponse createService(ServiceCreateRequest request, UUID userId) {
        User user = userService.findUserById(userId);

        Service service = new Service();

        createAdv(service,
                request.fullName(), request.description(),
                request.city(), request.address(),
                user, AdvType.SERVICE, request.options());

        service.setSpecialCategory(request.specialCategory());
        service.setCostOfPart(request.costOfPart());
        service.setTypeOfPart(request.typeOfPart());

        Service saved = serviceRepository.save(service);
        return toAdvDetailResponse(saved);
    }

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
            throw new AdvertisementIsNotAvailableException("آگهی نامعتبر است، فروخته یا غیرفعال است");
        }
        return toAdvDetailResponse(adv);
    }

    public List<AdvSummaryResponse> getUserAds(UUID userId) {
        List<Adv> ads = advRepository.findByUserId(userId);
        return ads.stream()
                .map(this::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }

    private void updateAdv(
            Adv adv, String fullName, String description,
            City city, String address, UUID userId, List<OptionRequest> options) {
        validateOwnership(adv, userId);

        if (fullName != null) adv.setFullName(fullName);
        if (description != null) adv.setDescription(description);
        if (city != null) adv.setCity(city);
        if (address != null) adv.setAddress(address);

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

    public AdvDetailResponse updateProduct(UUID advId, ProductUpdateRequest request, UUID userId) {
        Product product = (Product) findAdvById(advId);

        updateAdv(product,
                request.fullName(), request.description(),
                request.city(), request.address(),
                userId, request.options());

        if (request.stateOfProduct() != null) product.setStateOfProduct(request.stateOfProduct());
        if (request.brand() != null) product.setBrand(request.brand());
        if (request.model() != null) product.setModel(request.model());
        if (request.constructor() != null) product.setConstructor(request.constructor());
        if (request.category() != null) product.setCategory(request.category());
        if (request.price() != null) product.setPrice(request.price());

        Product updated = productRepository.save(product);
        return toAdvDetailResponse(updated);
    }

    public AdvDetailResponse updateService(UUID advId, ServiceUpdateRequest request, UUID userId) {
        Service service = (Service) findAdvById(advId);

        updateAdv(service,
                request.fullName(), request.description(),
                request.city(), request.address(),
                userId, request.options());

        if (request.fullName() != null) service.setFullName(request.fullName());
        if (request.description() != null) service.setDescription(request.description());
        if (request.city() != null) service.setCity(request.city());
        if (request.address() != null) service.setAddress(request.address());
        if (request.specialCategory() != null) service.setSpecialCategory(request.specialCategory());
        if (request.costOfPart() != null) service.setCostOfPart(request.costOfPart());
        if (request.typeOfPart() != null) service.setTypeOfPart(request.typeOfPart());

        Service updated = serviceRepository.save(service);
        return toAdvDetailResponse(updated);
    }

    public void deleteAdv(UUID advId, UUID userId) {
        Adv adv = findAdvById(advId);
        validateOwnership(adv, userId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    public void markAsSold(UUID advId, UUID userId) {
        Adv adv = findAdvById(advId);
        validateOwnership(adv, userId);
        if (adv.getStatus() != AdvStatus.ACTIVE) {
            throw new AdvertisementIsAlreadySoldException("این آگهی هم اکنون فروخته و غیرفعال شده است!");
        }
        adv.setStatus(AdvStatus.SOLD);
        advRepository.save(adv);
    }

    public void approveAdv(UUID advId) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.PENDING) {
            throw new AdvertisementIsNotAvailableException("آگهی در وضعیت انتظار نیست");
        }
        adv.setStatus(AdvStatus.ACTIVE);
        advRepository.save(adv);
    }

    public void rejectAdv(UUID advId, String reason) {
        Adv adv = findAdvById(advId);
        if (adv.getStatus() != AdvStatus.PENDING) {
            throw new AdvertisementIsNotAvailableException("آگهی در وضعیت انتظار نیست");
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

    public Adv findAdvById(UUID advId) {
        return advRepository.findById(advId)
                .orElseThrow(() -> new AdvertisementIsNotAvailableException("آگهی یافت نشد"));
    }

    private void validateOwnership(Adv adv, UUID userId) {
        if (!adv.getUser().getId().equals(userId)) {
            throw new IllegalOwnershipException("اجازه دسترسی به آگهی ندارید");
        }
    }

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

        if (adv.getAdvType() == AdvType.PRODUCT && adv instanceof Product p) {
            productDetail = new ProductDetailResponse(
                    p.getStateOfProduct(),
                    p.getBrand(),
                    p.getModel(),
                    p.getConstructor(),
                    p.getCategory(),
                    p.getPrice()
            );
        } else if (adv.getAdvType() == AdvType.SERVICE && adv instanceof Service s) {
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