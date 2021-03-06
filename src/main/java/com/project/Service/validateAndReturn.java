package com.project.Service;

import com.project.entities.Users;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class validateAndReturn {
    private SessionFactory factory;
    //we need to give Session factory to dao
    public validateAndReturn(SessionFactory factory){
        this.factory=factory;
    }

    public boolean validateUserDiff( String accountNo){
        Users user = null;
        try {
            Session session = this.factory.openSession();
            String hql = "from Users where accountNo=:accountNo";
            Query query = session.createQuery(hql);
            query.setParameter("accountNo", accountNo);
            user = (Users) query.uniqueResult();
            session.close();
        }
        catch (Exception e){
            return false;
        }
        return user != null;
    }

    public boolean validateUserMoney(String userName, String balance) {
        Users user = null;
        try {
            Session session = this.factory.openSession();
            String hql = "from Users where userName=:userName";
            Query query = session.createQuery(hql);
            query.setParameter("userName", userName);
            user = (Users) query.uniqueResult();
            session.close();
            if(Double.parseDouble(user.getBalance())<Double.parseDouble(balance))  {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
    public String transferMoney(String amount, Users user, String accountNo){
        double currentUserBalance = Double.parseDouble(user.getBalance());
        double transferUserBalance = Double.parseDouble(amount);
        double newUserBalance = currentUserBalance - transferUserBalance;
        String newBalance = Double.toString(newUserBalance);

        try {
            //deducting the balance from current user
            Session session = this.factory.openSession();
            Transaction transaction = session.beginTransaction();
            user.setBalance(newBalance);
            transaction.commit();
            session.update(user);

         //get the different user by accountNumber and increase the balance
            String hql1 = "from Users where accountNo=:accountNo";
            Query query1 = session.createQuery(hql1);
            query1.setParameter("accountNo", accountNo);
            Users user1 = (Users) query1.uniqueResult();
            if(user1==null){
                return "User not found";
            }
            double currentUser1Balance = Double.parseDouble(user1.getBalance());
            double newUser1Balance = currentUser1Balance + transferUserBalance;
            String newBalance1 = Double.toString(newUser1Balance);

            transaction=session.beginTransaction();
            user1.setBalance(newBalance1);
            transaction.commit();
            session.update(user1);

            session.close();
        }
        catch (Exception e) {
            return "Error";
        }
        return "Success";
    }
}
