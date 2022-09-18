package com.dolbik.springcourse.Project2Boot.services;

import com.dolbik.springcourse.Project2Boot.models.Book;
import com.dolbik.springcourse.Project2Boot.models.Person;
import com.dolbik.springcourse.Project2Boot.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;
    private final PeopleService peopleService;

    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleService peopleService) {
        this.booksRepository = booksRepository;
        this.peopleService = peopleService;
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(Sort.by("year"));
        } else
            return booksRepository.findAll();
    }

    public List<Book> findAllWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        } else
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public Book findOne(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        //достаем книгу по id из БД
        Book bookToBeUpdated = booksRepository.findById(id).get();

        //назначаем книге из формы id книги из БД
        updatedBook.setId(id);
        //назначаем книге из формы хозяина книги из БД
        //т.к. пользователь не вводит хозяина, там по умолчанию лежит null
        updatedBook.setOwner((bookToBeUpdated.getOwner()));
        //назначаем время передачи, чтобы оно также не обнулилось при обновлении книги
        updatedBook.setDateOfAssignment(bookToBeUpdated.getDateOfAssignment());

        booksRepository.save(updatedBook);//сохраняем книгу
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.map(Book::getOwner).orElse(null);
        //с помощью метода map находим владельца, если он есть, возвращем его
        //если нет, возвращаем null
    }

    @Transactional
    public void release(int id) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setDateOfAssignment(null);
                    book.setOwner(null);
                });
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setDateOfAssignment((Calendar.getInstance()));
                });
    }

    public List<Book> findByTitleStartingWith(String startingWith) {
        return booksRepository.findByTitleStartingWith(startingWith);
    }

}
