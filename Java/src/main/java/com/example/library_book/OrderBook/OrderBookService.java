package com.example.library_book.OrderBook;

import com.example.library_book.Book.Book;
import com.example.library_book.Book.BookRepository;
import com.example.library_book.Book.Category.Category;
import com.example.library_book.Book.Category.CategoryRepository;
import com.example.library_book.Config.JwtService;
import com.example.library_book.Customer.Customer;
import com.example.library_book.Customer.CustomerRepository;
import com.example.library_book.Dto.MemberData;
import com.example.library_book.Dto.OrderBookDto;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Image.Image;
import com.example.library_book.Image.ImageRepository;
import com.example.library_book.JwtDecode.JwtDecode;
import com.example.library_book.Oauth2.LoginStatus;
import com.example.library_book.Oauth2.Oauth2;
import com.example.library_book.Oauth2.Oauth2Repository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrderBookService {
    @Autowired
    public OrderBookRepository orderBookRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private JwtDecode jwtDecode;
    @Autowired
    private Oauth2Repository oauth2Repository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private String orderNo(){
        Random random = new Random();
        return "orderNo" +random.nextInt(1000);
    }

    public OrderBookDto createOrderBook(int bookId, String token, String loginStatus,int quantity){
        Optional<Book> book = bookRepository.findById(bookId);
        MemberData memberData = jwtDecode.decode(token,loginStatus);
        if (loginStatus.equals(LoginStatus.Google) && loginStatus.equals(LoginStatus.Github)){
            Oauth2 oauth2 = oauth2Repository.findByUserIdAndEmail(String.valueOf(memberData.getId()),memberData.getSub());
            OrderBookDto orderBookDto = convetDataOrder(book,quantity,bookId, Integer.parseInt(oauth2.getUserId()));
            return orderBookDto;

        }else {
            Customer customer = customerRepository.findByEmailAndId(memberData.getSub(),memberData.getId());
            OrderBookDto orderBookDto = convetDataOrder(book,quantity,bookId, customer.getId());
            return orderBookDto;

        }
    }

    private OrderBookDto convetDataOrder(Optional<Book>book,int quantity,int bookId,int userId) {
        book.stream().map(book1 -> {
            List<Image> image = imageRepository.findByBookId(book1).orElseThrow(null);
            System.out.println(image);
            Category category = categoryRepository.findById(book1.getCategoryId())
                    .orElseThrow(() -> new FindByNoId("Category not found with ID: " + book1.getCategoryId()));
            String orderNo = orderNo();
            orderBookRepository.save(OrderBook
                    .builder()
                    .orderNo(orderNo)
                    .bookPrice(book1.getBookPrice())
                    .categoryName(category.getCategoryName())
                    .bookName(book1.getBookName())
                    .img(image.get(0).getImg())
                    .userId(userId)
                    .orderStatus(OrderStatus.OK)
                    .quantity(quantity)
                    .totalMoney(book1.getBookPrice() + quantity)
                    .build());
            OrderBookDto orderBookDto = new OrderBookDto();
            orderBookDto.setBookName(book1.getBookName());
            orderBookDto.setOrderNo(orderNo);
            orderBookDto.setUserId(userId);
            orderBookDto.setBookPrice(book1.getBookPrice());
            orderBookDto.setBookId(bookId);
            orderBookDto.setAuthor(book1.getAuthor());
            orderBookDto.setCategoryName(category.getCategoryName());
            orderBookDto.setImageName(image.stream().map(Image::getImg).collect(Collectors.toList()));
            return orderBookDto;
        }).collect(Collectors.toList());
    return null;
    }
}
