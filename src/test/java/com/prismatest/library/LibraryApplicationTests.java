package com.prismatest.library;

import com.prismatest.library.repository.BookRepository;
import com.prismatest.library.repository.BorrowTransactionRepository;
import com.prismatest.library.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class LibraryApplicationTests {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected BorrowTransactionRepository borrowTransactionRepository;


    public final String book = "Title,Author,Genre,Publisher\n"
        + "\"Age of Discontuinity, The\",\"Drucker, Peter\",economics,Random House\n"
        + "\"Age of Wrath, The\",\"Eraly, Abraham\",history,Penguin\n"
        + "Aghal Paghal,\"Deshpande, P L\",nonfiction,Mauj\n"
        + "Ahe Manohar Tari,\"Deshpande, Sunita\",nonfiction,Mauj\n"
        + "All the President's Men,\"Woodward, Bob\",history,Random House\n"
        + "\"Amulet of Samarkand, The\",\"Stroud, Jonathan\",fiction,Random House\n"
        + "\"Analysis, Vol I\",\"Tao, Terence\",mathematics,HBA\n"
        + "Angels & Demons,\"Brown, Dan\",fiction,Random House\n"
        + "\"Argumentative Indian, The\",\"Sen, Amartya\",nonfiction,Picador\n"
        + "\"Artist and the Mathematician, The\",\"Aczel, Amir\",science,HighStakes\n"
        + "Asami Asami,\"Deshpande, P L\",fiction,Mauj";


    public final String user = "Name,First name,Member since,Member till,Gender\n"
        + "Aexi,Liam,01-01-2010,,m\n"
        + "Zhungwang,Noah,11/24/2020,,m\n"
        + "Odum,Oliver,05-08-1999,01-01-2021,m\n"
        + "Chish,Elijah,07-08-2006,,m\n"
        + "Jayi,William,11-12-2010,,m";

    public final String borrowed = "Borrower,Book,borrowed from,borrowed to\n"
        + "\"Chish,Elijah\",Data Smart,05/14/2008,05/29/2008\n"
        + "\"Zhungwang,Ava\",Birth of a Theorem,06/28/2021,07/13/2021\n"
        + "\"Jumummaaq,James\",Integration of the Indian States,08/29/2004,09/12/2004\n"
        + "\"Odum,Oliver\",\"Hunchback of Notre Dame, The\",07/30/2019,08/20/2019\n"
        + "\"Zhungwang,Noah\",\"Prisoner of Birth, A\",12/30/2020,01/25/2021\n"
        + "\"Barret-Kingsley,Emma\",Machine Learning for Hackers,11/25/2014,12/19/2014\n"
        + "\"Barret-Kingsley,Emma\",Radiowaril Bhashane & Shrutika,07/25/2003,08/19/2003\n"
        + "\"Aexi,Liam\",\"Complete Sherlock Holmes, The - Vol II\",05/09/2015,05/31/2015\n"
        + "\"Oomxii,Sophia\",False Impressions,04/17/2010,05/17/2010\n"
        + "\"Barret-Kingsley,Emma\",New Markets & Other Essays,06/15/2007,07/16/2007";

}
