package com.web.eventos.services;

import com.web.eventos.entities.Banner;
import com.web.eventos.repositories.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final MidiaService midiaService;

    public List<Banner> getBanners() {
        return bannerRepository.findAll();
    }

    public Banner salvar(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Banner salvarComUpload(MultipartFile file, String titulo) throws IOException {
        String url = midiaService.uploadArquivo(file).getUrl();

        Banner banner = Banner.builder()
                .titulo(titulo)
                .url(url)
                .build();

        return bannerRepository.save(banner);
    }

    public void excluir(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner não encontrado"));

        // Remover arquivo físico
        String filename = banner.getUrl().replace("/uploads/", "");
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads").resolve(filename);
            java.nio.file.Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Erro ao remover arquivo físico do banner: " + e.getMessage());
        }

        bannerRepository.delete(banner);
    }

    public Banner findById(Integer id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner não encontrado"));
    }
}