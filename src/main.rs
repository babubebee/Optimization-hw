mod simplex_method_impl {
    use std::{collections::HashMap, f64::MAX};
    use itertools::Itertools;
    const EPS_DEFAULT: f64 = 0.1;

    fn print_init(c: &Vec<f64>, a: &Vec<Vec<f64>>, b: &Vec<f64>, e: Option<f64>) {
        print!("max (or min) z = ");
        print!("({}) * x1", c[0]);
        for i in 1..c.len() {
            print!(" + ({}) * x{}", c[i], i + 1);
        }
        println!();
        println!("subject to the constraints:");
        for i in 0..a.len() {
            print!("{}) ", i + 1);
            let mut f = false;
            for j in 0..a[i].len() {
                if a[i][j] != 0.0 {
                    if !f { f = true; }
                    else  { print!(" + "); }
                    print!("({}) * x{}", a[i][j], j + 1);
                }
            }
            println!(" <= {}", b[i]);
        }
        match e {
            None => { println!("With precision {} (default)", EPS_DEFAULT); },
            Some(_) => { println!("With precision {}", e.unwrap());  }
        }
        
    }
    #[derive(Debug)]
    struct Table {
        data: Vec<Vec<f64>>,
        rows: HashMap<String, usize>,
        columns: HashMap<String, usize>,
        x: usize,
        s: usize,
        eps: f64
    }
    pub enum RetState {
        Solved,
        Unbounded
    }
    enum State {
        Unbound,
        Finished,
        InProgress
    }
    impl Table {
        fn print(&self) {
            for i in 1..self.x + 1 {
                print!("\t x{i}");
            }
            for i in 1..self.s + 1 {
                print!("\t s{i}");
            }
            println!("\t value");
         
            for j in self.rows.keys().sorted().rev() {
                print!("{j}");
                for i in 1..self.x + 1 {
                    print!("\t {:.2}", self.get(j, format!("x{i}").as_str()));
                }
                for i in 1..self.s + 1 {
                    print!("\t {:.2}", self.get(j, format!("s{i}").as_str()));
                }
                println!("\t {:.2}", self.get(j, "v"));
            }
        }
        fn get(&self, a : &str, b : &str) -> f64 {
            self.data[*self.rows.get(a).expect(format!("Failed to get {}", a).as_str())][*self.columns.get(b).expect("Failed")]
        }
        fn change_row(&mut self, go_in: &str, go_out: &str) {
            if self.get(go_out, go_in) == 0.0 {
                panic!("Cannot change row, 0 found");
            }
            if let Some(v) = self.rows.remove(go_out) {
                self.rows.insert(String::from(go_in), v);
            }
            let coef = self.get(go_in, go_in);
            for i in self.columns.keys() {
                let v = self.get(go_in, i) / coef;
                self.data[*self.rows.get(go_in).expect("Failed")][*self.columns.get(i).expect("Failed")] = v;
            }
            for i in self.rows.keys() {
                if i != go_in {
                    let coef = self.get(i, go_in);
                    for j in self.columns.keys() {
                        let v = self.get(i, j) - coef * self.get(go_in, j);
                        self.data[*self.rows.get(i).expect("Failed")][*self.columns.get(j).expect("Failed")] = v;
                    }
                }
            }
        }
        fn var_change(&self) -> (String, String, State) {
            let mut m = -self.eps;
            let mut s = String::new();
            for i in 1..self.x + 1 {
                if self.get("z", format!("x{i}").as_str()) < m {
                    m = self.get("z", format!("x{i}").as_str());
                    s = format!("x{i}");
                }                
            }
            if s.is_empty() { return (String::new(), String::new(), State::Finished) }
            let mut n: f64 = MAX;
            let mut out = String::new();
            for i in self.rows.keys() {
                if i == "z" { continue; }
                if self.get(i.as_str(), "v") / self.get(i.as_str(), s.as_str()) <= n && self.get(i.as_str(), s.as_str()) >= 0.0 {
                    n = self.get(i.as_str(), "v") / self.get(i.as_str(), s.as_str());
                    out = i.clone();
                }
            }
            if out.is_empty() { return (String::new(), String::new(), State::Unbound) }
            let go_in = s;
            let go_out = out;
            (go_in, go_out, State::InProgress)
        }
        fn init(c: &Vec<f64>, a: &Vec<Vec<f64>>, b: &Vec<f64>, eps: f64) -> Self {
            let mut table = Self {
                data: Vec::new(),
                rows: HashMap::new(),
                columns: HashMap::new(),
                x: c.len(),
                s: b.len(),
                eps
            };
            for i in 1..c.len() + 1 {
                table.columns.insert(format!("x{}", i), i - 1);
            }
            for i in 1..a.len() + 1 {
                table.columns.insert(format!("s{}", i),table.columns.len());
            }
            table.columns.insert(String::from("v"), table.columns.len());
            table.rows.insert(String::from("z"), 0);
            for i in 1..a.len() + 1 {
                table.rows.insert(format!("s{}", i), i);
            }
            // Now fill data
            table.data.resize(table.rows.len(), vec![0.0;table.columns.len()]);
            for i in 0..table.x {
                table.data[0][i] = -c[i];
            }
            for i in 1..table.s + 1 {
                for j in 0..table.x {
                    table.data[i][j] = a[i - 1][j];
                }
            }
            for i in 1..table.s + 1 {
                table.data[i][table.x + i - 1] = 1.0;
            }
            for i in 1..table.s + 1 {
                table.data[i][table.columns.len() - 1] = b[i - 1];
            }
            table
        }
        fn result(&self) -> f64 {
            self.data[0][self.data[0].len() - 1]
        }
        fn result_vec(&self) -> HashMap<String, f64> {
            let mut ans  = HashMap::new();
            for i in self.rows.keys() {
                if i.starts_with("x") {
                    ans.insert(i.clone(),self.get(i, "v"));
                }
            }
            ans
        }
    }

    pub fn simplex_method(c: Vec<f64>, a: Vec<Vec<f64>>, b: Vec<f64>, e: Option<f64>) -> (RetState, Option<HashMap<String, f64>>, Option<f64>) {
        print_init(&c, &a, &b, e);
        let e = e.unwrap_or(EPS_DEFAULT);
        let mut table = Table::init(&c, &a, &b, e);
        let mut it = 0;
        println!("Initial table:");
        table.print();
        loop {
            println!("-----------");
            let (a, b, c) = table.var_change();
            match c {
                State::InProgress => {},
                State::Unbound => {
                    // println!("The problem is unbounded!");
                    return (RetState::Unbounded, None, None);
                },
                State::Finished => { 
                    // println!("Finished!");
                    break
                }
            }
            table.change_row(a.as_str(), b.as_str());
            println!("Iteration #{}:", it);
            println!("{} enters, {} leaves!", a, b);
            table.print();
            it += 1;
        }
        (RetState::Solved, Some(table.result_vec()), Some(table.result()))
    }
}

use itertools::Itertools;
use simplex_method_impl::{simplex_method, RetState};

fn print_ans((a, b, c): (RetState, Option<std::collections::HashMap<String, f64>>, Option<f64>)) {
    match a {
        RetState::Solved => {
            println!("Solved!");
            let b = b.unwrap();
            let c = c.unwrap();
            println!("The maximum is {:.2} and the solution is:", c);
            for i in b.keys().sorted() {
                println!("{i} = {:.2}", b.get(i).unwrap_or(&0.0));
            }
        },
        RetState::Unbounded => {
            println!("Could not solve the problem since it is unbounded!");
        }
    }
}

fn test_1() {
    println!("Test 1:");
    let c = vec![9.0, 10.0, 16.0];
    let a = vec![
        vec![-18.0, -15.0, -12.0],
        vec![-6.0, 0.0, 0.0],
        vec![-5.0, -3.0, 3.0]
    ];
    let b = vec![360.0, 192.0, 180.0];
    let ans = simplex_method(c, a, b, None);
    print_ans(ans);
}

fn test_2() {
    println!("Test 2:");
    let c = vec![-1.0, 3.0, -3.0];
    let a = vec![
        vec![3.0, -1.0, -2.0],
        vec![-2.0, -4.0, 4.0],
        vec![1.0, 0.0, 1.0],
        vec![-2.0, 2.0, 1.0],
        vec![3.0, 0.0, 0.0],
    ];
    let b = vec![7.0, 3.0, 4.0, 8.0, 5.0];
    let ans = simplex_method(c, a, b, None);
    print_ans(ans);
}

fn main() {
    test_1();
    test_2();
}