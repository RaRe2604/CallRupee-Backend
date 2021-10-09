package com.raredevelopers.callrupee.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.raredevelopers.callrupee.ConfigUtil;
import com.raredevelopers.callrupee.constant.Process;
import com.raredevelopers.callrupee.model.Balance;
import com.raredevelopers.callrupee.model.User;
import com.raredevelopers.callrupee.repository.BalanceRepository;
import com.raredevelopers.callrupee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class UserController {

    final static float DURATION_TIME_TO_AMOUNT = 1800.0f;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private ConfigUtil configUtil;

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = new ArrayList<>(userRepository.findAll());
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{phone}")
    public ResponseEntity<User> getUserByPhone(@PathVariable("phone") String phone) {
        Optional<User> userData = userRepository.findByPhone(phone);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            Optional<User> userFromDB = userRepository.findByPhone(user.getPhone());
            if (userFromDB.isPresent()) {
                return new ResponseEntity<>(userFromDB.get(), HttpStatus.OK);
            } else {
                User _user = userRepository.save(new User(user.getName(), user.getPhone(), user.getEmail(), true, false,0.0f));
                return new ResponseEntity<>(_user, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user/{phone}")
    public ResponseEntity<User> updateUser(@PathVariable("phone") String phone, @RequestBody User user) {
        Optional<User> userData = userRepository.findByPhone(phone);

        if (userData.isPresent()) {
            User _user = userData.get();
            _user.setName(user.getName());
            _user.setPhone(user.getPhone());
            _user.setActive(user.isActive());
            return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") String id) {
        try {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//  @DeleteMapping("/user")
//  public ResponseEntity<HttpStatus> deleteUser() {
//    try {
//      userRepository.deleteAll();
//      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    } catch (Exception e) {
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }

    @PatchMapping("/user/{phone}")
    public ResponseEntity<User> updateStatus(@PathVariable("phone") String phone, @RequestBody User user) {
        Optional<User> userData = userRepository.findByPhone(phone);

        if (userData.isPresent()) {
            User _user = userData.get();
            _user.setActive(user.isActive());
            return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/balance/{phone}")
    public ResponseEntity<String> getBalanceByUserId(@PathVariable("phone") String phone) {
        Optional<User> userData = userRepository.findByPhone(phone);
        return userData.map(user -> new ResponseEntity<>(String.format("%.2f", user.getBalance()), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/balance/{phonenumber}")
    public ResponseEntity<String> updateBalance(@PathVariable("phonenumber") String phone, @RequestBody Balance userBalance) {
        Optional<User> userData = userRepository.findByPhone(phone);

        if (userData.isPresent() && userData.get().isActive()) {
            User _user = userData.get();
            Balance balance = new Balance();

            float balanceAmount = 0.0f;
            if ((userBalance.getDuration() == 0)) {
                //WITHDRAW
                balanceAmount = _user.getBalance() + userBalance.getAmount();
                balance.setAmount(Float.parseFloat(String.format("%.2f", Math.abs(userBalance.getAmount()))));
            } else {
                //DEPOSIT
                float limit = Float.parseFloat(configUtil.getProperty("DURATION_TIME_TO_AMOUNT"));
                balanceAmount = _user.getBalance() + Float.parseFloat(String.format("%.2f", (userBalance.getDuration() / limit)));
                balance.setAmount(Float.parseFloat(String.format("%.2f", (userBalance.getDuration() / limit))));
            }
            balanceAmount = Float.parseFloat(String.format("%.2f", balanceAmount));

            if (balanceAmount < 0) {
                return new ResponseEntity<>("Can't allow as " + userBalance.getAmount() + " is greater than you balance (" + _user.getBalance() + ").", HttpStatus.NOT_ACCEPTABLE);
            }

            balance.setFromPhone(userBalance.getFromPhone());
            balance.setToPhone(userBalance.getToPhone());
            balance.setDuration(userBalance.getDuration());
            balance.setProcess(userBalance.getAmount() < 0 ? Process.DEBIT_REQUEST : Process.CREDIT);

            balanceRepository.save(balance);

            _user.setBalance(balanceAmount);
            userRepository.save(_user);
            return new ResponseEntity<>("" + balanceAmount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("0.00", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/record/{phone}")
    public ResponseEntity<List<Balance>> getRecords(@PathVariable("phone") String phone) {
        List<Balance> result = balanceRepository.findByFromPhone(phone);
        return new ResponseEntity<>(result, result.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/record/{phone}/{process}")
    public ResponseEntity<List<Balance>> getRecordsByProcess(@PathVariable("phone") String phone, @PathVariable("process") Process process) {
        List<Balance> balances = balanceRepository.findByFromPhone(phone);
        List<Balance> result = new ArrayList<>();
        for (Balance balance : balances) {
            if (balance.getProcess().equals(process)) {
                result.add(balance);
            }
        }
        return result.isEmpty() ? new ResponseEntity<>(result, HttpStatus.NOT_FOUND) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/records/{process}")
    public ResponseEntity<List<Balance>> getRecordsByOneProcess(@PathVariable("process") Process process) {
        List<Balance> result = balanceRepository.findByProcess("DEBIT_REQUEST");
        return result.isEmpty() ? new ResponseEntity<>(result, HttpStatus.NOT_FOUND) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/about")
    public ResponseEntity<String> getAbout() {
//        String about = "<h1 style=\"text-align: center;\">Call Rupee App</h1><h4>Version: v1.0.0</h4><p>Call Rupee is the best earning app , fully secure and very easy to use for users .<br />There will be no issues in payments to users but it will take some time to send money to your bank account so don't worry about it.</p><p><span style=\"color: #ff0000;\">Note:</span> Its takes maximum 1 day to send money.</p><p>You can directly interact with our service head to solve your problems and doubts by these below links:<br /><br /><strong>Help:</strong></p><p>Email: <a href=\"mailto:kamsu.saikrishnasahu@gmail.com\">kamsu.saikrishnasahu@gmail.com</a></p><p>Link us Instagram: <a href=\"https://instagram.com/call_rupee\">https://instagram.com/call_rupee</a></p>";
        String about = configUtil.getProperty("about");
        return new ResponseEntity<>(about, HttpStatus.OK);
    }

    @PatchMapping("/records/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable("id") String id) {
        Optional<Balance> _balance = balanceRepository.findById(id);

        if (_balance.isPresent()) {
            Balance balance = _balance.get();
            balance.setProcess(Process.DEBIT_SUCCESSFUL);
            balanceRepository.save(balance);
            return new ResponseEntity<>("TRUE", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("FALSE", HttpStatus.NOT_FOUND);
        }
    }

}
