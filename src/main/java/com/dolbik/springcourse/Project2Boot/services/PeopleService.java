package com.dolbik.springcourse.Project2Boot.services;

import com.dolbik.springcourse.Project2Boot.models.Book;
import com.dolbik.springcourse.Project2Boot.models.Person;
import com.dolbik.springcourse.Project2Boot.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;
    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }
    public List<Person> findAll(){
        return peopleRepository.findAll();
    }
    public Person findOne(int id){
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElse(null);
    }
    @Transactional
    public void save(Person person){
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id, Person updatedPerson){
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }
    @Transactional
    public void delete(int id){

        peopleRepository.deleteById(id);
    }
    public Optional<Person> findByFullName(String fullName){
        return peopleRepository.findByFullName(fullName);
    }
    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Hibernate.initialize((person.get().getBooks()));

            person.get().getBooks().forEach(book -> {
                Calendar tenDaysAfterAssignment = book.getDateOfAssignment();
                tenDaysAfterAssignment.add(Calendar.DAY_OF_MONTH, 10);
                Calendar currentDay = Calendar.getInstance();
                if (currentDay.after(tenDaysAfterAssignment)) {
                    book.setOverdue(true);
                }
            });
            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
        //через Date
        //получаем разницу в миллисекундах между текущим временем и датой назначения книги
        //long diffInMS = new Date().getTime() - book.getTakenAt().getTime()
        //if (diffInMs > 864000000) book.setExpired(true);
    }
}
