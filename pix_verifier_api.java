package com.example.pixverifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.persistence.*;
import java.util.*;

@SpringBootApplication
public class PixVerifierApplication {
    public static void main(String[] args) {
        SpringApplication.run(PixVerifierApplication.class, args);
    }
}

@Entity
class PixTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderCpf;
    private String receiverCpf;
    private String pixKey;
    private Double amount;
    private boolean isFraud;

    public PixTransaction() {}

    public PixTransaction(String senderCpf, String receiverCpf, String pixKey, Double amount, boolean isFraud) {
        this.senderCpf = senderCpf;
        this.receiverCpf = receiverCpf;
        this.pixKey = pixKey;
        this.amount = amount;
        this.isFraud = isFraud;
    }

    public Long getId() { return id; }
    public String getSenderCpf() { return senderCpf; }
    public String getReceiverCpf() { return receiverCpf; }
    public String getPixKey() { return pixKey; }
    public Double getAmount() { return amount; }
    public boolean getIsFraud() { return isFraud; }

    public void setSenderCpf(String senderCpf) { this.senderCpf = senderCpf; }
    public void setReceiverCpf(String receiverCpf) { this.receiverCpf = receiverCpf; }
    public void setPixKey(String pixKey) { this.pixKey = pixKey; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setIsFraud(boolean isFraud) { this.isFraud = isFraud; }
}

interface PixRepository extends JpaRepository<PixTransaction, Long> {
    Optional<PixTransaction> findByPixKey(String pixKey);
}

@RestController
@RequestMapping("/api/pix")
class PixController {

    @Autowired
    private PixRepository repository;

    @GetMapping
    public List<PixTransaction> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public PixTransaction getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @PostMapping
    public PixTransaction create(@RequestBody PixTransaction transaction) {
        // Simulação simples de verificação de fraude:
        // Exemplo: se a chave PIX contém a palavra "fake" ou valor acima de 10000, é fraude.
        if (transaction.getPixKey().toLowerCase().contains("fake") || transaction.getAmount() > 10000) {
            transaction.setIsFraud(true);
        } else {
            transaction.setIsFraud(false);
        }
        return repository.save(transaction);
    }

    @PutMapping("/{id}")
    public PixTransaction update(@PathVariable Long id, @RequestBody PixTransaction updatedTransaction) {
        PixTransaction existing = repository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        existing.setSenderCpf(updatedTransaction.getSenderCpf());
        existing.setReceiverCpf(updatedTransaction.getReceiverCpf());
        existing.setPixKey(updatedTransaction.getPixKey());
        existing.setAmount(updatedTransaction.getAmount());
        existing.setIsFraud(updatedTransaction.getIsFraud());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "Transaction deleted successfully";
    }

    @GetMapping("/verify/{pixKey}")
    public Map<String, Object> verifyPix(@PathVariable String pixKey) {
        Map<String, Object> response = new HashMap<>();
        Optional<PixTransaction> transaction = repository.findByPixKey(pixKey);

        if (transaction.isPresent()) {
            response.put("pixKey", pixKey);
            response.put("isFraud", transaction.get().getIsFraud());
        } else {
            response.put("pixKey", pixKey);
            response.put("isFraud", false);
            response.put("message", "Pix key not found, seems legitimate");
        }

        return response;
    }
}
