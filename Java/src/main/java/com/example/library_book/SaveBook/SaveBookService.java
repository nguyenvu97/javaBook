package com.example.library_book.SaveBook;

import com.example.library_book.Book.Book;
import com.example.library_book.Book.BookRepository;
import com.example.library_book.Book.Category.Category;
import com.example.library_book.Book.Category.CategoryRepository;
import com.example.library_book.Customer.Customer;
import com.example.library_book.Customer.CustomerRepository;
import com.example.library_book.Dto.MemberData;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Image.Image;
import com.example.library_book.Image.ImageRepository;
import com.example.library_book.JwtDecode.JwtDecode;
import com.example.library_book.Oauth2.LoginStatus;
import com.example.library_book.Oauth2.Oauth2;
import com.example.library_book.Oauth2.Oauth2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaveBookService {
    @Autowired
    public SaveBookRepository saveBookRepository;
    @Autowired
    public JwtDecode jwtDecode;
    @Autowired
    public Oauth2Repository oauth2Repository;
    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    public BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    public ImageRepository imageRepository;

    public SaveBook addSaveBook(int bookId,String token,String loginStatus){
        if (bookId <= 0 && token == null ){
            throw new FindByNoId("not found" + token);
        }
        MemberData memberData = jwtDecode.decode(token,loginStatus);
        if (loginStatus.equals(LoginStatus.Google) || loginStatus.equals(LoginStatus.Github)){
            Oauth2 oauth2 = oauth2Repository.findByUserIdAndEmail(String.valueOf(memberData.getId()),memberData.getSub());
            convetSaveBookOauth(bookId,oauth2);
        }else {
            Customer customer = customerRepository.findByEmailAndId(memberData.getSub(),memberData.getId());
            convetSaveBookCustomer(bookId,customer);
        }

      return null;
    }
    public void convetSaveBookOauth(int bookId,Oauth2 oauth2){
        Optional<Book> book = bookRepository.findById(bookId);
        book.stream().map(book1->{
            Category category = categoryRepository.findById(book1.getCategoryId()).orElseThrow(()-> new FindByNoId("not found" + book1.getCategoryId()));
            List<Image> images = imageRepository.findByBookId(book1).orElseThrow(null);

            return saveBookRepository.save(SaveBook
                    .builder()
                    .bookCategory(category.getCategoryName())
                    .bookId(book1.getId())
                    .bookName(book1.getBookName())
                    .mp4(book1.getMp4())
                    .pathMp4(book1.getPathMp4())
                    .imageName(images.get(0).getImg())
                    .userId(Integer.parseInt(oauth2.getUserId()))
                    .build());
        }).collect(Collectors.toList());
    }
    public void convetSaveBookCustomer(int bookId,Customer oauth2){
        Optional<Book> book = bookRepository.findById(bookId);
        book.stream().map(book1->{
            Category category = categoryRepository.findById(book1.getCategoryId()).orElseThrow(()-> new FindByNoId("not found" + book1.getCategoryId()));
            List<Image> images = imageRepository.findByBookId(book1).orElseThrow(null);

            return saveBookRepository.save(SaveBook
                    .builder()
                    .bookCategory(category.getCategoryName())
                    .bookId(book1.getId())
                    .bookName(book1.getBookName())
                    .mp4(book1.getMp4())
                    .pathMp4(book1.getPathMp4())
                    .imageName(images.get(0).getImg())
                    .userId(oauth2.getId())
                    .build());
        }).collect(Collectors.toList());
    }
}
