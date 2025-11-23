package com.web.eventos.services;

import com.web.eventos.entities.Evento;
import com.web.eventos.entities.Local;
import com.web.eventos.entities.Midia;
import com.web.eventos.entities.MidiaTipo;
import com.web.eventos.repositories.MidiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MidiaService {
    private final MidiaRepository midiaRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm");

    /**
     * Faz upload de um arquivo sem associação (para uso genérico como avatar, logo,
     * etc)
     */
    public Midia uploadArquivo(MultipartFile file) throws IOException {
        validarArquivo(file);
        String url = salvarArquivoNoServidor(file);
        MidiaTipo tipo = determinarTipoMidia(file.getContentType());

        Midia midia = Midia.builder()
                .tipo(tipo)
                .url(url)
                .build();

        return midiaRepository.save(midia);
    }

    /**
     * Faz upload de um arquivo associado a um evento
     */
    public Midia uploadArquivo(MultipartFile file, Evento evento) throws IOException {
        validarArquivo(file);
        String url = salvarArquivoNoServidor(file);
        MidiaTipo tipo = determinarTipoMidia(file.getContentType());

        Midia midia = Midia.builder()
                .evento(evento)
                .tipo(tipo)
                .url(url)
                .build();

        return midiaRepository.save(midia);
    }

    /**
     * Faz upload de um arquivo associado a um local
     */
    public Midia uploadArquivo(MultipartFile file, Local local) throws IOException {
        validarArquivo(file);
        String url = salvarArquivoNoServidor(file);
        MidiaTipo tipo = determinarTipoMidia(file.getContentType());

        Midia midia = Midia.builder()
                .local(local)
                .tipo(tipo)
                .url(url)
                .build();

        return midiaRepository.save(midia);
    }

    private void validarArquivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo está vazio");
        }

        // Validação de tamanho máximo (10MB)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("O arquivo excede o tamanho máximo permitido de %d MB",
                            MAX_FILE_SIZE / (1024 * 1024)));
        }

        // Validação de tipo (apenas imagens e vídeos)
        String contentType = file.getContentType();
        if (!validarTipo(contentType)) {
            throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido. Apenas imagens (JPEG, PNG, GIF, WebP) e vídeos (MP4, MPEG, MOV, AVI, WebM) são aceitos");
        }
    }

    private String salvarArquivoNoServidor(MultipartFile file) throws IOException {
        // Criar diretório se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Gerar nome único para o arquivo
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Salvar arquivo no servidor
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retornar URL relativa do arquivo
        return "/uploads/" + filename;
    }

    private boolean validarTipo(String contentType) {
        if (contentType == null) {
            return false;
        }
        return ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase()) ||
                ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());
    }

    private MidiaTipo determinarTipoMidia(String contentType) {
        if (contentType == null) {
            return MidiaTipo.IMAGEM;
        }
        if (ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            return MidiaTipo.VIDEO;
        }
        return MidiaTipo.IMAGEM;
    }

    public void removerMidia(Integer id) {
        Midia midia = midiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada"));

        try {
            String filename = midia.getUrl().replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Erro ao remover arquivo físico: " + e.getMessage());
        }

        midiaRepository.delete(midia);
    }

    public List<Midia> buscarPorEvento(Integer eventoId) {
        return midiaRepository.findByEventoId(eventoId);
    }

    public List<Midia> buscarPorLocal(Integer localId) {
        return midiaRepository.findByLocalId(localId);
    }

    public Midia buscarPorId(Integer id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada"));
    }
}
