# TCDD Bilet Kontrol Uygulaması

## 🧩 Özellikler

* Kalkış/varış istasyonlarına ve zaman aralığına göre tren bileti arama.
* Arama sonuçlarını koltuk tipine (vagon sınıfı) göre filtreleme.
* Müsait koltuk bulunduğunda e-posta bildirimi gönderme.

## 🗂️ Proje Yapısı

Proje, standart bir Spring Boot yapısını takip eder:

src/main/java/com/tcdd/ticketcheck/
├── config
│   └── RestClientConfig.java     # Spring RestClient için yapılandırma
├── controller
│   └── TicketCheckController.java  # Bilet arama uç noktası için REST Denetleyici
├── dto
│   ├── FilteredTrainDetails.java   # Filtrelenmiş arama sonuçları için DTO
│   ├── TicketRequest.java          # TCDD API'sine gönderilen istek gövdesi için DTO
│   ├── TicketResponse.java         # TCDD API'sinden alınan yanıt gövdesi için DTO
│   └── TicketSearchRequest.java    # İstemciden gelen istek için DTO
└── service
├── EmailService.java         # E-posta bildirimleri göndermek için Servis
└── TicketCheckService.java   # API çağrıları ve filtrelemeyi yöneten ana servis

## ⚙️ Yapılandırma

`src/main/resources` klasöründe bir `application.properties` dosyasını düzenleyerek  uygulamayı yapılandırın. TCDD API ayrıntılarını ve e-posta yapılandırmasını sağlamanız gerekecektir.

## 🌐 API Uç Noktası
POST /ticket/check
Sağlanan arama kriterlerine göre müsait tren biletlerini kontrol eder.

İstek Gövdesi: application/json
TicketSearchRequest DTO'sunu kullanır:

JSON
{
  "departureTime": "gg-AA-yyyy SS:mm:ss",
  "departureTimeEnd": "gg-AA-yyyy SS:mm:ss",
  "departureStationName": "string",
  "arrivalStationName": "string",
  "departureStationId": 0,
  "arrivalStationId": 0,
  "seatType": "string" 
}

Yanıt: application/json
Bir List<FilteredTrainDetails> içeren bir ResponseEntity döndürür.

JSON
[
  {
    "trainName": "string",
    "trainNumber": "string",
    "departureStationName": "string",
    "arrivalStationName": "string",
    "departureTime": "gg-AA-yyyy SS:mm:ss",
    "arrivalTime": "gg-AA-yyyy SS:mm:ss",
    "seatType": "string",
    "availableSeats": 0
  }
]

Kriterlere uyan müsait koltuklar bulunursa, liste bu trenlerin detaylarını içerecektir. Ayrıca yapılandırılan alıcı e-posta adresine bir bildirim e-postası gönderilecektir.
