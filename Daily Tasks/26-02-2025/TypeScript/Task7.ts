 type Employee = {
    id: number;
    empname: string;
    email: string;
  };
  type Point = {
    x: number;
    y: number;
  };
  const employee1: Employee = {
    id: 1,
    empname: "Madhu",
    email: "madhu@gmail.com",
  };
  
  const employee2: Employee = {
    id: 2,
    empname: "suresh",
    email: "suresh@gmail.com",
  };
  
  const originPoint: Point = {
    x: 0,
    y: 0,
  };
  
  function printStudentInfo(employee: Employee) {
    console.log(`Student ID: ${employee.id}`);
    console.log(`Studentname: ${employee.empname}`);
    console.log(`Email: ${employee.email}`);
  }
  
  function calculateDistance(point1: Point, point2: Point): number {
    const dx = point1.x - point2.x;
    const dy = point1.y - point2.y;
    return Math.sqrt(dx * dx + dy * dy);
  }

  console.log("Employee 1:");
  printStudentInfo(employee1);
  
  console.log("\nStudent 2:");
  printStudentInfo(employee2);
  
  console.log("\nDistance from origin:");
  console.log(calculateDistance(originPoint, { x: 3, y: 4 }));
  