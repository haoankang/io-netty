package ank.hao.io.protobuf.demo;

import ank.hao.io.protobuf.code.AddressBookProtos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProtoDemo {

    public static void main(String[] args) throws IOException {

        //encode
        AddressBookProtos.Person john = AddressBookProtos.Person.newBuilder()
                .setId(111)
                .setName("John De")
                .setEmail("john@example.com")
                .addPhones(
                        AddressBookProtos.Person.PhoneNumber.newBuilder()
                                .setNumber("555-2322").setType(AddressBookProtos.Person.PhoneType.HOME)
                                .build()
                )
                .build();

        AddressBookProtos.AddressBook.Builder addressBook = AddressBookProtos.AddressBook.newBuilder();
        addressBook.addPeople(john);

        String file = "src/main/resources/john";
        FileOutputStream outputStream = new FileOutputStream(file);
        addressBook.build().writeTo(outputStream);
        outputStream.close();

        //decode
        FileInputStream inputStream = new FileInputStream(file);
        AddressBookProtos.AddressBook addressBook1 = AddressBookProtos.AddressBook.parseFrom(inputStream);
        System.out.println(addressBook1);
    }
}
