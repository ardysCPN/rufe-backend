// C:\microservicio-rufe\rufe\src\main\java\co\rufe\rufe\model\MenuItemPermiso.java
package co.rufe.rufe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemPermiso {
    private Integer menuItemId; 
    private Integer permisoId;  
}