package com.dolbik.springcourse.Project2Boot.controllers;

import com.dolbik.springcourse.Project2Boot.models.Book;
import com.dolbik.springcourse.Project2Boot.models.Person;
import com.dolbik.springcourse.Project2Boot.services.BooksService;
import com.dolbik.springcourse.Project2Boot.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final PeopleService peopleService;
    private final BooksService booksService;

    @Autowired
    public BooksController(PeopleService peopleService, BooksService booksService){
        this.peopleService = peopleService;
        this.booksService = booksService;
    }
    @GetMapping()
    public String showAllBooks(Model model,
                               @RequestParam(value="page", required=false) Integer page,
                               @RequestParam(value="books_per_page", required=false) Integer booksPerPage,
                               @RequestParam(value="sort_by_year", required=false) boolean sortByYear) {
        if(page == null || booksPerPage == null){
            model.addAttribute("books", booksService.findAll(sortByYear));
        }else{
            model.addAttribute("books", booksService.findAllWithPagination(page, booksPerPage, sortByYear));
        }
        return "books/allBookPages";
    }
    @GetMapping("/{id}")
    public String showOneBook(@PathVariable("id") int id, Model model,
                              @ModelAttribute("person") Person person){
        model.addAttribute("book", booksService.findOne(id));
        Person bookOwner = booksService.getBookOwner(id);

        if (bookOwner != null) {
            model.addAttribute("owner", bookOwner);
        }else {
            model.addAttribute("people", peopleService.findAll());
        }
        return "books/oneBook";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book){
        return "books/new";
    }
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }
    @PostMapping()
    public String creat(@ModelAttribute("book") @Valid Book book,
                        BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "books/new";
        }
        booksService.save(book);
        return "redirect:/books";
    }
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book updatedBook,
                         BindingResult bindingResult,
                         @PathVariable("id") int id){
        if(bindingResult.hasErrors()){
            return "books/edit";
        }
        booksService.update(id, updatedBook);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);
        return "redirect:/books/"+id;
    }
    //у selectedPerson назначено только поле id, остальные поля null
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id,
                                 @ModelAttribute("personToAssign") Person selectedPerson) {
        booksService.assign(id, selectedPerson);
        return "redirect:/books/"+id;
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        booksService.delete(id);
        return "redirect:/books";
    }
    @GetMapping("/search")
    public String searchPage(){
        return "books/search";
    }
    @PostMapping("/search")
    public String makeSearch(Model model,
                         @RequestParam(value="starting_with", required=false) String startingWith){
        model.addAttribute("books",booksService.findByTitleStartingWith(startingWith));
        return "books/search";
    }
}
