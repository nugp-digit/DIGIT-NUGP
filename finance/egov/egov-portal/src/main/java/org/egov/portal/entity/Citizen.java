/** eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.portal.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.egov.infra.admin.master.entity.Device;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.persistence.entity.enums.UserType;

@Entity
@Table(name = "egp_citizen")
public class Citizen extends User {

    private static final long serialVersionUID = -521416613072970524L;

    private String activationCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "eg_userdevice", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "deviceid"))
    private Set<Device> devices = new HashSet<>();

    public Citizen() {
        setType(UserType.CITIZEN);
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(final String activationCode) {
        this.activationCode = activationCode;
    }

    public Set<Device> getDevices() {
        return devices;
    }

    public void setDevices(final Set<Device> devices) {
        this.devices = devices;
    }

}
